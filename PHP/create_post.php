<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_GET['group']) && isset($_GET['username']) && !empty($_GET['contents'])) {
 
    $group = $_GET['group'];
    $uname = $_GET['username'];
    $contents = $_GET['contents'];
    
    
    //$sql = "INSERT INTO Posts (GroupName, Username, Contents) VALUES ('$group', '$uname', '$contents')";
    
    if (isset($_GET['imageOneID'])) {
		$image_one_id = $_GET['imageOneID'];
		$sql = "INSERT INTO Posts (GroupName, Username, Contents, ImageOne) VALUES ('$group', '$uname', '$contents', '$image_one_id')";
	
		if (isset($_GET['imageTwoID'])) {
			$image_two_id = $_GET['imageTwoID'];
			$sql = "INSERT INTO Posts (GroupName, Username, Contents, ImageOne, ImageTwo) VALUES ('$group', '$uname', '$contents', '$image_one_id' , '$image_two_id')";
		
			if (isset($_GET['imageThreeID'])) {
				$image_three_id = $_GET['imageThreeID'];
				$sql = "INSERT INTO Posts (GroupName, Username, Contents, ImageOne, ImageTwo, ImageThree) VALUES ('$group', '$uname', '$contents', '$image_one_id' , '$image_two_id', '$image_three_id')";
			}
		}
	}
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
    
    // sql for inserting into db
    $sql = sprintf("INSERT INTO Posts (GroupName, Username, Contents) VALUES ('%s', '%s', '%s')", mysqli_real_escape_string($con, $group), mysqli_real_escape_string($con, $uname), mysqli_real_escape_string($con, $contents));

	// check if row inserted or not
    if (mysqli_query($con, $sql)) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Successfully posted in " . $group;
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred and the post was not created";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
}
else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing";
 
    // echoing JSON response
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
