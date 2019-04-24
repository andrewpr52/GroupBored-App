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
	
	if (isset($_GET['username'])) {
		$uname = $_GET['username'];
		
		$query1 = sprintf("SELECT ID, Username, UserBio, JoinDate, ProfilePictureURL FROM Users WHERE Username='%s'", mysqli_real_escape_string($con, $uname));
		$result = mysqli_query($con, $query1);
		$result2 = mysqli_query($con, sprintf("SELECT COUNT(ID) AS TotalPosts FROM Posts WHERE Username = '%s'", mysqli_real_escape_string($con, $uname)));
		$result3 = mysqli_query($con, sprintf("SELECT GroupName, COUNT(ID) AS numPosts FROM Posts WHERE Username = '%s' GROUP BY GroupName ORDER BY numPosts DESC", mysqli_real_escape_string($con, $uname)));
		
		if (!empty($result)) {
			$userData = mysqli_fetch_all($result);
			$numPostData = mysqli_fetch_assoc($result2);
			$favoriteGroupData = mysqli_fetch_assoc($result3);
			
			$user = array();
			
			$user["ID"] = str_replace("&quot;", "'", htmlspecialchars($userData[0][0]));
			$user["Username"] = str_replace("&quot;", "'", htmlspecialchars($userData[0][1]));
			$user["UserBio"] = $userData[0][2];
			$user["JoinDate"] = date("m/d/Y", strtotime(str_replace("&quot;", "'", htmlspecialchars($userData[0][3]))));
			$user["ProfilePictureURL"] = str_replace("&quot;", "'", htmlspecialchars($userData[0][4]));
			$user["NumPosts"] = str_replace("&quot;", "'", htmlspecialchars($numPostData['TotalPosts']));
			$user["FavoriteGroup"] = str_replace("&quot;", "'", htmlspecialchars($favoriteGroupData['GroupName']));
			
			
			$response["success"] = 1;
			$response["user"] = array();
			array_push($response["user"], $user);
 
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
