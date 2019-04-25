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
	
	if (isset($_GET['postID'])) {
		$post_id = $_GET['postID'];
		
		$result1 = mysqli_query($con, "SELECT URL FROM Images JOIN Posts ON (Images.ID = Posts.ImageOne) WHERE Posts.ID = '$post_id'");
		$result2 = mysqli_query($con, "SELECT URL FROM Images JOIN Posts ON (Images.ID = Posts.ImageTwo) WHERE Posts.ID = '$post_id'");
		$result3 = mysqli_query($con, "SELECT URL FROM Images JOIN Posts ON (Images.ID = Posts.ImageThree) WHERE Posts.ID = '$post_id'");
		
		if (mysqli_num_rows($result1) > 0) {
			$rows = mysqli_fetch_all($result1);
			
			$url = str_replace("&quot;", "'", htmlspecialchars($rows[0][0]));
			
			$response["success"] = 1;
			$response["URL1"] = $url;
			
			
			if (mysqli_num_rows($result2) > 0) {
				$rows = mysqli_fetch_all($result2);
				
				$url = str_replace("&quot;", "'", htmlspecialchars($rows[0][0]));
				
				$response["URL2"] = $url;
	 
				
				if (mysqli_num_rows($result3) > 0) {
					$rows = mysqli_fetch_all($result3);
					
					$url = str_replace("&quot;", "'", htmlspecialchars($rows[0][0]));
					
					$response["URL3"] = $url;
				}
			}
			
 
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
