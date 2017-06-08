package com.gearreald.tullfileclient.job;

import java.io.IOException;

import com.gearreald.tullfileclient.models.Piece;
import com.gearreald.tullfileclient.worker.WorkerException;

public class VerifyPiece extends Job {

	private static final String JOB_NAME="Verify piece %d of file %s.";
	private Piece piece;
	private boolean done;
	
	public VerifyPiece(Piece piece){
		super();
		this.piece=piece;
		this.done=false;
	}
	@Override
	public void theJob() throws WorkerException {
		try {
			this.piece.verifyPiece();
			this.done = true;
		} catch (IOException e) {
			throw new WorkerException("Failed to verify piece.",e);
		}
	}

	@Override
	public String getJobName() {
		return String.format(JOB_NAME, this.piece.getPieceNumber(), this.piece.getParentFile().getName());
	}

	@Override
	public boolean completed() {
		return done;
	}

}
