package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DBService {
	
    private static String dbUrl;
    /**
	 * @return the dbUsername
	 */
	public static String getDbUsername() {
		return dbUsername;
	}

	/**
	 * @return the dbPassword
	 */
	public static String getDbPassword() {
		return dbPassword;
	}
	/**
	 * @return the dbUrl
	 */
	public String getDbUrl() {
		return dbUrl;
	}

	private static String dbUsername;

    private static String dbPassword;
    
    @Autowired
    public DBService(
    		@Value("${db.url}") String dbUrl,
    		@Value("${db.username}") String dbUsername,
    		@Value("${db.password}") String dbPassword ) {
    	
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

	public DBService() {
		super();
	}


}
