<?php
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

$contenido = file_get_contents("php://input");

if(isset($contenido)){
    
    $data = json_decode($contenido, true);

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $stmt = $conexion->prepare("UPDATE tblMascota SET desaparecida = ? WHERE idMascota = ?");
    $stmt->bindValue(1,filter_var($data["desaparecida"], FILTER_VALIDATE_BOOLEAN));
    $stmt->bindValue(2,$data["idMascota"]);
    
    if($stmt->execute()){ 
        $stmt=null;  
        http_response_code(200);
    }
    else{
        http_response_code(304);
    }
}
else{
    http_response_code(304);
} 
?>
