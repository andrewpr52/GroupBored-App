<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
	// array for JSON response
	$response = array();

	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
	
	if (mysqli_connect_errno($con)) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}
	
	if (isset($_GET['imagename'])) {
		$image_name = $_GET['imagename'];
		
		$result = mysqli_query($con, "SELECT ID FROM Images WHERE URL='$image_name'");
		
		if (!empty($result)) {
			$rows = mysqli_fetch_all($result);
			
			$idnum = str_replace("&quot;", "'", htmlspecialchars($rows[0][0]));
			
			$response["success"] = 1;
			$response["ID"] = $idnum;
 
            // echoing JSON response
            echo json_encode($response, JSON_PRETTY_PRINT);
		}
	}
	else {
		// required field is missing
		$response["success"] = 0;
		$response["message"] = "Required field(s) missing";
	 
		// echoing JSON response
		echo json_encode($response);
	}
	mysqli_close($con);
?>
