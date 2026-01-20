package it.unina.bugboard.login;

import it.unina.bugboard.app.BugBoard;
import it.unina.bugboard.common.SessionManager;
import it.unina.bugboard.navigation.SceneRouter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ApplicationTest {

    @Mock
    private LoginApiService loginApiService;

    @Mock
    private SessionManager sessionManager;

    private LoginController controller;

    @Override
    public void start(Stage stage) throws IOException {
        SceneRouter.inizializza(stage);

        FXMLLoader loader = new FXMLLoader(BugBoard.class.getResource("/it/unina/bugboard/fxml/login.fxml"));

        controller = new LoginController(loginApiService, sessionManager);

        loader.setControllerFactory(param -> {
            if (param == LoginController.class) {
                return controller;
            }
            try {
                return param.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testLoginButtonDisabledInitially() {
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#pulsanteLogin", NodeMatchers.isDisabled());
    }

    @Test
    void testLoginButtonEnabledWhenFieldsFilled() {
        clickOn("#campoUsername").write("testuser");
        clickOn("#campoPassword").write("password");

        FxAssert.verifyThat("#pulsanteLogin", NodeMatchers.isEnabled());
    }

    @Test
    void testLoginFailureShowsError() {
        RispostaLogin rispostaFallita = Mockito.mock(RispostaLogin.class);
        when(rispostaFallita.isSuccess()).thenReturn(false);
        when(rispostaFallita.getMessage()).thenReturn("Credenziali errate");
        when(loginApiService.login("utente_sbagliato", "password_sbagliata")).thenReturn(rispostaFallita);
        clickOn("#campoUsername").write("utente_sbagliato");
        clickOn("#campoPassword").write("password_sbagliata");
        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);
        clickOn("#pulsanteLogin");
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#errorePassword", LabeledMatchers.hasText("Credenziali errate"));
        FxAssert.verifyThat("#errorePassword", NodeMatchers.isVisible());
    }
}
