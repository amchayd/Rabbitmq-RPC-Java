package microservices.pojo;

import java.io.Serializable;

public class Response implements Serializable{
	
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String msg;
	private Status status;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status success) {
		this.status = success;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	public static enum Status {
		SUCCESS,
		ERROR
		}
}
