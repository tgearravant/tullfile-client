package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import net.tullco.tullutils.FileUtils;

public class Piece implements Comparable<Piece> {
	private File pieceFile;
	private int pieceNumber;
	private TullFile parentFile;
	private boolean verified;
	private boolean invalid;
	
	public Piece(TullFile parentFile, int pieceNumber, File pieceFile){
		this.pieceFile = pieceFile;
		this.parentFile = parentFile;
		this.pieceNumber = pieceNumber;
		this.verified=false;
		this.invalid=false;
	}
	public byte[] getData() throws IOException{
		return FileUtils.getFileAsBytes(this.pieceFile);
	}
	public TullFile getParentFile(){
		return this.parentFile;
	}
	public boolean verified(){
		return this.verified;
	}
	public boolean invalid(){
		return this.invalid;
	}
	public boolean verifyPiece() throws IOException{
		try{
			String serverHash =
					ServerConnection.getFilePieceHash(
							this.getParentFile().getLocalPath()
							,this.getParentFile().getName()
							,this.pieceNumber);
			String localHash = FileUtils.sha1Hash(this.pieceFile);
			if(localHash.equals(serverHash)){
				this.verified=true;
			}else{
				this.invalid=true;
			}
		}catch(NoSuchAlgorithmException e){
			ErrorDialogBox.dialogFor(e);
		}
		return this.verified();
	}
	public boolean deletePiece() {
		return this.pieceFile.delete();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentFile == null) ? 0 : parentFile.hashCode());
		result = prime * result + pieceNumber;
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
		Piece other = (Piece) obj;
		if (parentFile == null) {
			if (other.parentFile != null)
				return false;
		} else if (!parentFile.equals(other.parentFile))
			return false;
		if (pieceNumber != other.pieceNumber)
			return false;
		return true;
	}
	@Override
	public int compareTo(Piece compared) {
		if(this.equals(compared)){
			return 0;
		}else{
			Integer thisPiece = new Integer(this.pieceNumber);
			Integer comparedPiece = new Integer(compared.pieceNumber);
			return thisPiece.compareTo(comparedPiece);
		}
	}
}
