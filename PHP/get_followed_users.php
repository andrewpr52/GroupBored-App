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
	
	if (isset($_GET['parentid'])) {
		$parentid = $_GET['parentid'];
		
		$result = mysqli_query($con, "SELECT Users.ID, Username FROM FollowedUsers JOIN Users ON (FollowedUserID = Users.ID) WHERE ParentUserID = '$parentid'");
		if (!empty($result)) {
			$rows = mysqli_fetch_all($result);
			
			$followed_users = array();
			
			for ($x=0; $x<count($rows); $x++) {
				$followed_users[$x]["ID"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][0]));
				//$followed_users[$x]["Username"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][1]));
			}
			
			$response["success"] = 1;
			$response["followed_users"] = array();
			array_push($response["followed_users"], $followed_users);
 
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
