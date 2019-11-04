
$filename =  time() . '.jpg';
$filepath = 'images/';
if(!is_dir($filepath))
mkdir($filepath);
if(isset($_FILES['webcam'])){   
move_uploaded_file($_FILES['webcam']['tmp_name'], $filepath.$filename);

echo $filepath.$filename;
}
