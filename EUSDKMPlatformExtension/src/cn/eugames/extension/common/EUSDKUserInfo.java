package cn.eugames.extension.common;

public class EUSDKUserInfo {
	public enum loginType {
		NormalLogin, AnonymousLogin
	}

	public enum regType {
		AnonymousReg, PhoneReg, CustomReg, EmailReg
	}

	private String token;
	private String ID = "";
	private String name;
	private String reg_type = regType.CustomReg.toString();
	private String login_type = loginType.NormalLogin.toString();

	private String refreshToken ;
	public String getToken() {
		return token;
	}

	
	public String getRefreshToken() {
		return refreshToken;
	}


	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	public void setToken(String token) {
		this.token = token;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReg_type() {
		return reg_type;
	}

	public void setReg_type(regType reg_type) {
		this.reg_type = reg_type.toString();
	}

	public String getLogin_type() {
		return login_type;
	}

	public void setLogin_type(loginType login_type) {
		this.login_type = login_type.toString();
	}

}
