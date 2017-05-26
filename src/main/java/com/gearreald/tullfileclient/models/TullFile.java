package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;

import com.gearreald.tullfileclient.job.DownloadTullFilePiece;
import com.gearreald.tullfileclient.job.VerifyAndMergeFile;
import com.gearreald.tullfileclient.job.VerifyPiece;
import com.gearreald.tullfileclient.worker.WorkerQueues;

import net.tullco.tullutils.FileUtils;

public class TullFile {
	
	private String name;
	private TullFolder parent;
	private int pieceCount;
	private List<Piece> pieceList;
	
	public TullFile(JSONObject json,TullFolder parent){
		pieceList = new CopyOnWriteArrayList<Piece>();
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
	public String getSuffix(){
		String fileName = this.getName();
		int i = fileName.lastIndexOf('.');

		if (i > 0) {
		    return fileName.substring(i+1);
		}else{
			return "*";
		}
	}
	public String getLocalPath(){
		return this.parent.getLocalPath();
	}
	public int getPieceCount(){
		return this.pieceCount;
	}
	public boolean deleteFile(){
		try {
			ServerConnection.deleteFile(this.getLocalPath(), this.getName());
			return true;
		} catch (IOException e) {
			ErrorDialogBox.dialogFor(e);
			return false;
		}
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
		Piece p = new Piece(this, piece, f);
		this.pieceList.add(p);
		this.pieceList.sort(null);
		WorkerQueues.addJobToQueue("quick", new VerifyPiece(p));
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
		cleanUpAllPieces();
	}
	public void cleanUpAllPieces(){
		for(Piece p: this.pieceList){
			p.deletePiece();
		}
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
