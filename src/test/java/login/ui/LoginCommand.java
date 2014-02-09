package login.ui;

import org.springframework.util.StringUtils;

/**
* Created with IntelliJ IDEA.
* User: hongseongmin
* Date: 2014. 2. 7.
* Time: 오후 5:04
* To change this template use File | Settings | File Templates.
*/
public class LoginCommand {
	private String id;
	private String password;

	public void setId(String id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public boolean validate() {
		return ! (StringUtils.isEmpty(id) || StringUtils.isEmpty(password));
	}
}
