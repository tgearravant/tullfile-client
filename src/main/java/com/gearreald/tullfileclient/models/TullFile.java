package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;

import com.gearreald.tullfileclient.job.DownloadTullFilePiece;
import com.gearreald.tullfileclient.job.VerifyAndMergeFile;
import com.gearreald.tullfileclient.job.VerifyPiece;
import com.gearreald.tullfileclient.worker.WorkerQueues;

import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.StringUtils;

public class TullFile implements TullObject, Comparable<TullFile> {
	
	private String name;
	private TullFolder parent;
	private int pieceCount;
	private List<Piece> pieceList;
	private long fileSize;
	
	public TullFile(JSONObject json,TullFolder parent){
		this.pieceList = new CopyOnWriteArrayList<Piece>();
		if(parent==null){
			ErrorDialogBox.dialogFor(new RuntimeException("A Tullfile must have a parent."));
		}
		this.parent=parent;
		this.update(json);
	}
	public void update(JSONObject json){
		this.name=json.getString("name");
		this.pieceCount=json.getInt("pieces");
		this.fileSize=json.getLong("size");
	}
	public String getName(){
		return this.name;
	}
	public long getFileSize(){
		return this.fileSize;
	}
	public String getFileSizeAsString(){
		int orderOfMagnitude = (int) (Math.log(fileSize)/Math.log(1024));
		double displayNumber=this.getFileSize()/Math.pow(1024d, orderOfMagnitude);
		String baseString = "%.2f%s";
		switch (orderOfMagnitude){
			case 0:
				return String.format(baseString, displayNumber, "B");
			case 1:
				return String.format(baseString, displayNumber, "KB");
			case 2:
				return String.format(baseString, displayNumber, "MB");
			case 3:
				return String.format(baseString, displayNumber, "GB");
			case 4:
				return String.format(baseString, displayNumber, "TB");
			case 5:
				return String.format(baseString, displayNumber, "PB");
			default:
				return "0B";
		}
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
	public double getDownloadProgress(){
		int verified=0;
		for(Piece p: this.pieceList){
			if(p.verified())
				verified++;
		}
		return (double)verified/this.pieceCount;
	}
	public boolean delete(){
		try {
			ServerConnection.deleteFile(this.getLocalPath(), this.getName());
			this.parent.removeFile(this);
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
			if(p.invalid()){
				this.pieceList.remove(p);
				p.deletePiece();
				WorkerQueues.addJobToQueue("download",new DownloadTullFilePiece(this,p.getPieceNumber()));
			}
		}
	}
	public File getPieceFile(int piece){
		File tempFolder = TempHandler.getTempDirectoryForFile(this.getLocalPath(), this.getName());
		if(!tempFolder.exists()){
			tempFolder.mkdirs();
		}
		File pieceLocation = new File(StringUtils.assureEndsWith(tempFolder.getAbsolutePath(),"/")+StringUtils.leftPad(Integer.toString(piece), '0', 8));
		return pieceLocation;
	}
	public void deletePiece(int pieceNumber){
		Iterator<Piece> iterator = this.pieceList.iterator();
		while(iterator.hasNext()){
			Piece p = iterator.next();
			if(p.getPieceNumber()==pieceNumber){
				p.deletePiece();
				this.pieceList.remove(p);
				break;
			}
		}
	}
	public void downloadPiece(int piece) throws IOException{
		File pieceLocation = this.getPieceFile(piece);
		Piece p;
		if(pieceLocation.exists()){
			 p = new Piece(this,piece,pieceLocation);
		}else{
			String localPath = this.getLocalPath();
			String fileName = this.getName();
			byte[] pieceData = ServerConnection.downloadFilePiece(localPath,fileName,piece);
			FileUtils.writeBytesToFile(pieceData, pieceLocation);
			p = new Piece(this, piece, pieceLocation);
		}
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
	public void reverifyPieces(){
		for(Piece p: this.pieceList){
			p.scheduleReverifyPiece();
		}
	}
	public String getFileHash(){
		try{
			String s = ServerConnection.getFileHash(this.getLocalPath(), this.getName());
			return s;
		}catch(IOException e){
			ErrorDialogBox.dialogFor(e);
			return null;
		}
	}
	public void mergePieces(File f) throws IOException{
		FileOutputStream output = new FileOutputStream(f);
		for(Piece p: this.pieceList){
			output.write(p.getData());
		}
		output.close();
	}
	public void cleanUpAllPieces(){
		for(Piece p: this.pieceList){
			p.deletePiece();
		}
		this.pieceList.clear();
	}
	public boolean isTullFolder(){
		return false;
	}
	public boolean isTullFile(){
		return true;
	}
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("name", this.name);
		json.put("pieces", this.pieceCount);
		json.put("size", this.fileSize);		
		return json;
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
	@Override
	public int compareTo(TullFile o) {
		if(this.equals(o))
			return 0;
		return this.name.toLowerCase().compareTo(o.name.toLowerCase());
	}
}
