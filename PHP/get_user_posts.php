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
		
		$sql = sprintf("SELECT * FROM Posts WHERE Username='%s'", mysqli_real_escape_string($con, $uname));
		$result = mysqli_query($con, $sql);
		
		if (!empty($result)) {
			$rows = mysqli_fetch_all($result);
			
			$sql2 = sprintf("SELECT ProfilePictureURL FROM Users WHERE Username = '%s'", mysqli_real_escape_string($con, $uname));
			$imgresult = mysqli_query($con, $sql2);
			$rows2 = mysqli_fetch_all($imgresult);
			
			$post = array();
					
			for ($x=0; $x<count($rows); $x++) {			
				$post[$x]["ID"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][0]));
				$post[$x]["Timestamp"] = date("h:ia m/d/Y", strtotime(str_replace("&quot;", "'", htmlspecialchars($rows[$x][1]))));
				$post[$x]["GroupName"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][2]));
				$post[$x]["Username"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][3]));
				$post[$x]["Contents"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][4]));
				$post[$x]["Upvotes"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][5]));
				$post[$x]["Downvotes"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][6]));
				$post[$x]["ProfilePictureURL"] = str_replace("&quot;", "'", htmlspecialchars($rows2[0][0]));
			}
			
			$response["success"] = 1;
			$response["post"] = array();
			array_push($response["post"], $post);
 
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
