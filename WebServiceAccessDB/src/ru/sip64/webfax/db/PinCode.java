package ru.sip64.webfax.db;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: PinCode
 *
 */
@Entity
@Table(name="pincode")
public class PinCode implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public PinCode() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="username", nullable=false, unique=true)
	private String username;
	
	@Column(name="pin_code", nullable=false, unique=true)
	private String pinCode;
	
	@Column(name="pin_password", nullable=false)
	private String pinPassword;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinPassword() {
		return pinPassword;
	}

	public void setPinPassword(String pinPassword) {
		this.pinPassword = pinPassword;
	}
	
	
   
}
