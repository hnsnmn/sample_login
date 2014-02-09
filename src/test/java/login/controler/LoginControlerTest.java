package login.controler;

import login.ui.LoginCommand;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: hongseongmin
 * Date: 2014. 2. 7.
 * Time: 오후 5:11
 * To change this template use File | Settings | File Templates.
 */
public class LoginControlerTest {

	public static final String PASSWORD = "password";
	public static final String USER_ID = "userId";
	private LoginControler loginControler;
	private MockHttpServletResponse mockResponse;
	private AutheService mockAuthService;

	@Before
	public void setUp() throws Exception {
		mockResponse = new MockHttpServletResponse();
		mockAuthService = mock(AutheService.class);
		loginControler = new LoginControler(mockAuthService);
	}

	// form 요청 처리
	@Test
	public void whenRequestIsForm_returnFormView() {
		String viewName = loginControler.form();
		assertThat(viewName, equalTo(loginControler.FORM_VIEW));
	}

	// form 전송시, LoginCommand 값 이상, form 뷰를 리턴한다.(비정상)
	@Test
	public void whenInvalidLoginCommand_returnFormView() {
		assertFormViewWhenInvalidLoginCommand(null, PASSWORD);
		assertFormViewWhenInvalidLoginCommand("", PASSWORD);
		assertFormViewWhenInvalidLoginCommand(USER_ID, null);
		assertFormViewWhenInvalidLoginCommand(USER_ID, "");
	}

	// form 전송시, ID가 존재하지 않으면 form 뷰를 리턴한다.(비정상)
	@Test
	public void whenNonExistingUser_returnFormView() {
		when(mockAuthService.authenticate("noUserId", PASSWORD)).thenThrow(new NonExistingUserException());
		assertFormViewWhenIdOrPwNotMatch("noUserId", PASSWORD);
	}

	// form 전송시, ID/PASSWORD 불일치 하면 form 뷰를 리턴한다.(비정상)
	@Test
	public void whenWrongPasword_returnFormView() {
		when(mockAuthService.authenticate(USER_ID, "wrongPassword")).thenThrow(new WrongPasswordException());
		assertFormViewWhenIdOrPwNotMatch(USER_ID, "wrongPassword");
	}

	// form 전송시, ID/PASSWORD 일치, success 뷰를 리턴하며, 추가로 쿠키 생성 확인
	@Test
	public void whenIdAndPwMatching_returnSuccessView() {
		when(mockAuthService.authenticate(USER_ID, PASSWORD)).thenReturn(new Authentication(USER_ID));
		String viewName = runSubmit(USER_ID, PASSWORD);
		assertThat(viewName, equalTo(LoginControler.SUCCESS_VIEW));

		assertThat(mockResponse.getCookie("AUTH").getValue(), equalTo(USER_ID));
	}

	private void assertFormViewWhenIdOrPwNotMatch(String userId, String password) {
		String viewName = runSubmit(userId, password);
		assertThat(viewName, equalTo(loginControler.FORM_VIEW));
	}

	private void assertFormViewWhenInvalidLoginCommand(String id, String password) {
		String viewName = runSubmit(id, password);
		assertThat(viewName, equalTo(loginControler.FORM_VIEW));
	}

	private String runSubmit(String id, String password) {
		LoginCommand loginCommand = createSpiedLoginCommnad(id, password);
		String viewName = loginControler.submit(loginCommand, mockResponse);
		verify(loginCommand).validate();
		return viewName;
	}

	private LoginCommand createSpiedLoginCommnad(String id, String password) {
		LoginCommand loginCommand = new LoginCommand();
		loginCommand.setId(id);
		loginCommand.setPassword(password);
		return spy(loginCommand);
	}

	private class LoginControler {

		private static final String SUCCESS_VIEW = "successview";
		private static final String FORM_VIEW = "formview";

		private AutheService authService;

		public LoginControler(AutheService mockAuthService) {
			this.authService = mockAuthService;
		}

		public String form() {
			return FORM_VIEW;
		}

		public String submit(LoginCommand loginCommand, HttpServletResponse response) {
			if (!loginCommand.validate())
				return FORM_VIEW;

			try {
				Authentication auth = authenticate(loginCommand);
				sendAuthCookie(response, auth);
				return SUCCESS_VIEW;
			} catch (NonExistingUserException e) {
				return FORM_VIEW;
			} catch (WrongPasswordException ex) {
				return FORM_VIEW;
			}

		}

		private void sendAuthCookie(HttpServletResponse response, Authentication auth) {
			response.addCookie(createAuthCookie(auth));
		}

		private Cookie createAuthCookie(Authentication auth) {
			return new Cookie("AUTH", auth.getId());
		}

		private Authentication authenticate(LoginCommand loginCommand) {
			return authService.authenticate(loginCommand.getId(), loginCommand.getPassword());
		}
	}


	private interface AutheService {
		Authentication authenticate(String id, String password);
	}
	private class NonExistingUserException extends RuntimeException {
	}

	private class Authentication {
		private final String id;

		public Authentication(String id) {
			this.id = id;
		}

		private String getId() {
			return id;
		}
	}

	private class WrongPasswordException extends RuntimeException {
	}
}
