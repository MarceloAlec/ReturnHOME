<?php
header("Content-Type: application/json");
//CREDENCIALES DE ACCESO A LA BASE DE DATOS
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

if(isset($_GET['idCliente'])){
    $idCliente = $_GET['idCliente'];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                   $username,
                                   $password,
                                   array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $consulta = "SELECT * FROM tblcliente WHERE idCliente = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$idCliente);
    $stmt->execute();
    $num_cliente= $stmt->rowCount();
    
    if($num_cliente == 1){
        $resultado = $stmt->fetch(PDO::FETCH_ASSOC);
        $stmt=null;
    
        http_response_code(200);

        echo json_encode(array("cliente" => array("idCliente" => $resultado["idCliente"],
                                                  "nombre" => $resultado["nombre"],
                                                  "email" => $resultado["email"],
                                                  "numeroCelular" => $resultado["numeroCelular"])));

    }
    else{
        http_response_code(404);
    }
}
else{
    http_response_code(404);
}

?>