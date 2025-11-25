package Utility.DB.PostgresImplementation;

import Utility.DB.InterfacceDB.InterfacciaConnessioneDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDBPostgreSQL implements InterfacciaConnessioneDB {

    private static ConnessioneDBPostgreSQL instance;
    private Connection connection;

    private static String url;
    private static String user;
    private static String password;

    private ConnessioneDBPostgreSQL() {
        try {
            loadConfig();
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connessione al database stabilita con successo.");
        } catch (SQLException e) {
            System.err.println("Errore durante la connessione al database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try (java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Spiacente, impossibile trovare config.properties");
                return;
            }
            java.util.Properties prop = new java.util.Properties();
            prop.load(input);

            url = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            password = prop.getProperty("db.password");

        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    } 

    public static synchronized ConnessioneDBPostgreSQL getInstance() {
        if (instance == null) {
            instance = new ConnessioneDBPostgreSQL();
        } else {
            try {
                if (instance.getConnessione() == null || instance.getConnessione().isClosed()) {
                    instance = new ConnessioneDBPostgreSQL();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    @Override
    public Connection getConnessione() {
        return connection;
    }

    @Override
    public void chiudiConnessioneDB() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione al database chiusa.");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
