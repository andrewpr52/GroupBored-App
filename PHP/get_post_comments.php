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
	
	if (isset($_GET['postid'])) {
		$postid = $_GET['postid'];
		
		$sql = sprintf("SELECT * FROM Comments WHERE PostID='%s'", mysqli_real_escape_string($con, $postid));
		$result = mysqli_query($con, $sql);
		
		if (!empty($result)) {
			$rows = mysqli_fetch_all($result);
			
			$comments = array();
					
			for ($x=0; $x<count($rows); $x++) {
				$username = $rows[$x][3];
				
				$sql2 = sprintf("SELECT ProfilePictureURL FROM Users WHERE Username = '%s'", mysqli_real_escape_string($con, $username));
				$imgresult = mysqli_query($con, $sql2);
				$rows2 = mysqli_fetch_all($imgresult);
					
				$comments[$x]["ID"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][0]));
				$comments[$x]["PostID"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][1]));
				$comments[$x]["Timestamp"] = date("h:ia m/d/Y", strtotime(str_replace("&quot;", "'", htmlspecialchars($rows[$x][2]))));
				$comments[$x]["Username"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][3]));
				$comments[$x]["Contents"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][4]));
				$comments[$x]["Upvotes"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][5]));
				$comments[$x]["Downvotes"] = str_replace("&quot;", "'", htmlspecialchars($rows[$x][6]));
				$comments[$x]["ProfilePictureURL"] = str_replace("&quot;", "'", htmlspecialchars($rows2[0][0]));
			}
			
			$response["success"] = 1;
			$response["comments"] = array();
			array_push($response["comments"], $comments);
 
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
