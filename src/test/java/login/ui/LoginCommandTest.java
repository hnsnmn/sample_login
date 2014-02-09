package login.ui;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: hongseongmin
 * Date: 2014. 2. 7.
 * Time: 오후 3:53
 * To change this template use File | Settings | File Templates.
 */
public class LoginCommandTest {

	public static final String USER_ID = "userId";
	public static final String PASSWORD = "1234";

	@Test
	public void testValidate() throws Exception {

		assertThat(createLoginCommand(null, PASSWORD).validate(), is(false));
		assertThat(createLoginCommand("", PASSWORD).validate(), is(false));
		assertThat(createLoginCommand(USER_ID, null).validate(), is(false));
		assertThat(createLoginCommand(USER_ID, "").validate(), is(false));
		assertThat(createLoginCommand(USER_ID, PASSWORD).validate(), is(true));

	}

	private LoginCommand createLoginCommand(String id, String password) {
		LoginCommand loginCommand = new LoginCommand();
		loginCommand.setId(id);
		loginCommand.setPassword(password);
		return loginCommand;
	}

}
