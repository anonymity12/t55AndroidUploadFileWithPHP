//upload.php, 运行在mac 端的服务程序，接受手机传来的图片

welcome<?php
echo $_POST["uploaded_file"];
if (isset($_FILES["uploaded_file"]["name"])) {
	echo "entering";
	echo "<br>";
	$name = $_FILES["uploaded_file"]["name"];
	$tmp_name = $_FILES['uploaded_file']['tmp_name'];
	$error = $_FILES['uploaded_file']['error'];

	if (!empty($name)) {
		$location = './assets/';
		echo "uploading..";

		if (!is_dir($location)) {
			mkdir($location);
			echo "mkdir";
		}
		if (move_uploaded_file($tmp_name, $location.$name))
			echo 'Uploaded';
	}
	else 
		echo 'plz choose a file';
}
?>
