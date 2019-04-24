<?php

class DB_CONNECT {
	function __construct() {
		$this->connect();
	}
	function __destruct() {
		$this->close();
	}
	function connect() {
		//require_once __DIR__ . '/db_config.php';
		
		// Connecting to mysql database
        $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard") or die(mysql_error());
 
        // returing connection cursor
        return $con;
		
		//$conn = new PDO("mysql:host=".DB_SERVER.";dbname=".DB_DATABASE.";charset=utf8", DB_USER, DB_PASSWORD);
		//$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		
		//return $conn;
	}
	function close() {
		mysqli_close();
		//$this = null;
	}
}

?>
