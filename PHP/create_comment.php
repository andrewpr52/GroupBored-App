<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_GET['postid']) && !empty($_GET['username']) && !empty($_GET['contents'])) {
 
    $postid = $_GET['postid'];
    $uname = $_GET['username'];
    $contents = $_GET['contents'];
    
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
    
    // sql for inserting into db
    $sql = sprintf("INSERT INTO Comments (PostID, Username, Contents) VALUES ('%s', '%s', '%s')", mysqli_real_escape_string($con, $postid), mysqli_real_escape_string($con, $uname), mysqli_real_escape_string($con, $contents));
 
	// check if row inserted or not
    if (mysqli_query($con, $sql)) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Comment successfully created.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred and the comment was not added.";
 
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
