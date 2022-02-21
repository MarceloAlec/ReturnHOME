<?php
header("Content-Type: application/json");
//CREDENCIALES DE ACCESO A LA BASE DE DATOS
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

    $consulta = "INSERT INTO tblMascota (nombre, raza, genero, descripcion, desaparecida, idCliente) values(?, ?, ?, ?, ?, ?)";

    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$data["nombre"]);
    $stmt->bindValue(2,$data["raza"]);
    $stmt->bindValue(3,$data["genero"]);
    $stmt->bindValue(4,$data["descripcion"]);
    $stmt->bindValue(5,filter_var($data["desaparecida"], FILTER_VALIDATE_BOOLEAN));
    $stmt->bindValue(6,$data["idCliente"]);
    
    if($stmt->execute()){
        $id=$conexion->lastInsertId();
        $stmt=null;
        http_response_code(201);
        echo json_encode(array("mascota" => array("idMascota"=>$id)));
    }
    else{
        http_response_code(401);
    }
}
else{
    http_response_code(401);
}
    
?>


