<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_GET['uid']) && isset($_GET['pid']) && isset($_GET['action'])) {
 
	$uid = $_GET['uid'];
    $pid = $_GET['pid'];
    $action = $_GET['action'];
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
 
	// mysql update post upvote count +1
	if ($action == 1) {
		$sql = sprintf("UPDATE Posts SET Upvotes = Upvotes + 1 WHERE id='%s'", mysqli_real_escape_string($con, $pid));
		$sql2 = sprintf("INSERT INTO Likes (UserID, PostID) VALUES ('%s', '%s')", mysqli_real_escape_string($con, $uid), mysqli_real_escape_string($con, $pid));
	}
	// mysql update post downvote count +1
	else if ($action == 2) {
		$sql = sprintf("UPDATE Posts SET Downvotes = Downvotes + 1 WHERE id='%s'", mysqli_real_escape_string($con, $pid));
		$sql2 = sprintf("INSERT INTO Dislikes (UserID, PostID) VALUES ('%s', '%s')", mysqli_real_escape_string($con, $uid), mysqli_real_escape_string($con, $pid));
	}
	// mysql update post upvote count -1
	else if ($action == 3) {
		$sql = sprintf("UPDATE Posts SET Upvotes = Upvotes - 1 WHERE id='%s'", mysqli_real_escape_string($con, $pid));
		$sql2 = sprintf("DELETE FROM Likes WHERE UserID = '%s' AND PostID = '%s'", mysqli_real_escape_string($con, $uid), mysqli_real_escape_string($con, $pid));
	}
	// mysql update post downvote count -1
	else if ($action == 4) {
		$sql = sprintf("UPDATE Posts SET Downvotes = Downvotes - 1 WHERE id='%s'", mysqli_real_escape_string($con, $pid));
		$sql2 = sprintf("DELETE FROM Dislikes WHERE UserID = '%s' AND PostID = '%s'", mysqli_real_escape_string($con, $uid), mysqli_real_escape_string($con, $pid));
	}
	
	// check if row inserted or not
    if (mysqli_query($con, $sql) && mysqli_query($con, $sql2)) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Post vote successful.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred and the post was not upvoted.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
}
else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing.";
 
    // echoing JSON response
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
