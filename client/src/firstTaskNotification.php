<?php
// API URL
$url = "http://localhost:8080/tasks/priority";

// Fetch data from API
$response = file_get_contents($url);

// Check if API call failed
if ($response === FALSE) {
    echo "Unable to fetch tasks";
    exit;
}

// Convert JSON to PHP array
$data = json_decode($response, true);

// Check if tasks exist
if (!empty($data) && isset($data[0])) {
    $firstTask = $data[0];

    $title = $firstTask['title'];
    $content = html_entity_decode($firstTask['content']);
} else {
    echo "No tasks found";
    exit;
}
?>

<!DOCTYPE html>
<html>
<head>
    <title>Task Notification</title>
    <style>
        body {
            font-family: Arial;
            background: #f5f5f5;
        }
        .notification {
            margin: 40px auto;
            width: 60%;
            padding: 20px;
            background: white;
            border-left: 6px solid #4CAF50;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h3 {
            margin: 0 0 10px 0;
        }
    </style>
</head>
<body>

<div class="notification">
    <h3>🔔 First Priority Task</h3>
    <strong><?php echo $title; ?></strong>
    <p><?php echo $content; ?></p>
</div>

</body>
</html>
