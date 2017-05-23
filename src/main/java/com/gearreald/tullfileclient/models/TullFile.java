package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import com.gearreald.tullfileclient.job.DownloadTullFilePiece;
import com.gearreald.tullfileclient.job.VerifyAndMergeFile;
import com.gearreald.tullfileclient.worker.WorkerQueues;

import net.tullco.tullutils.FileUtils;

public class TullFile {
	
	private String name;
	private TullFolder parent;
	private int pieceCount;
	private List<Piece> pieceList;
	
	public TullFile(JSONObject json,TullFolder parent){
		pieceList = new ArrayList<Piece>();
		if(parent==null){
			ErrorDialogBox.dialogFor(new RuntimeException("A Tullfile must have a parent."));
		}
		this.name=json.getString("name");
		this.pieceCount = json.getInt("pieces");
		this.parent=parent;
	}
	public String getName(){
		return this.name;
	}
	public String getLocalPath(){
		return this.parent.getLocalPath();
	}
	public int getPieceCount(){
		return this.pieceCount;
	}
	public boolean allPiecesDownloaded(){
		return this.pieceList.size()==this.pieceCount;
	}
	public boolean allPiecesValid(){
		for(Piece p: this.pieceList){
			if(!p.verified())
				return false;
		}
		return true;
	}
	public void trashInvalidPieces(){
		for(Piece p: this.pieceList){
			if(p.invalid())
				this.pieceList.remove(p);
		}
	}
	public void downloadPiece(File f, int piece) throws IOException{
		String localPath = this.getLocalPath();
		String fileName = this.getName();
		byte[] pieceData = ServerConnection.downloadFilePiece(localPath,fileName,piece);
		FileUtils.writeBytesToFile(pieceData, f);
		this.pieceList.add(new Piece(this, piece, f));
		this.pieceList.sort(null);
	}
	public void queueAllPiecesForDownload(File downloadLocation){
		for(int i=1; i<=this.getPieceCount(); i++){
			WorkerQueues.addJobToQueue("download", new DownloadTullFilePiece(this, i));
		}
		WorkerQueues.addJobToQueue("quick", new VerifyAndMergeFile(this,downloadLocation));
	}
	public void mergePieces(File f) throws IOException{
		FileOutputStream output = new FileOutputStream(f);
		for(Piece p: this.pieceList){
			output.write(p.getData());
		}
		output.close();
	}
	public void downloadFile() throws IOException{
		File f=new File("C:/Users/tgearr34/TullFile/test.txt");
		String localPath = this.getLocalPath();
		String fileName = this.getName();
		FileOutputStream output = new FileOutputStream(f);
		for(int i=1;i<=this.getPieceCount();i++){
			byte[] pieceData = ServerConnection.downloadFilePiece(localPath,fileName,i);
			String pieceHash;
			try {
				pieceHash = DatatypeConverter.printHexBinary(MessageDigest.getInstance("SHA-1").digest(pieceData));
				String serverHash = ServerConnection.getFilePieceHash(localPath, fileName, i);
				if(!pieceHash.equals(serverHash)){
					i--;
					ErrorDialogBox.dialogFor(new RuntimeException("Hash mismatch. Server: "+serverHash+" Local: "+pieceHash));
				}
			} catch (NoSuchAlgorithmException e) {
				ErrorDialogBox.dialogFor(e);
			}
			output.write(pieceData);
		}
		output.close();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TullFile other = (TullFile) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
}
