package Utility.DB.InterfacceDB;

import java.sql.Connection;

public interface InterfacciaConnessioneDB {
    Connection getConnessione();
    void chiudiConnessioneDB();
}