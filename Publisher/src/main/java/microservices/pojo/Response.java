package microservices.pojo;

import java.io.Serializable;

public class Response implements Serializable{
	
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static enum Status {
		SUCCESS,
		ERROR
		}
	private String msg;
	private String status;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
