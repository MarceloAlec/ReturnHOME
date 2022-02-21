<?php
header("Content-Type: application/json");
//CREDENCIALES DE ACCESO A LA BASE DE DATOS
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

if(isset($_GET["idCliente"])){
    $idCliente = $_GET["idCliente"];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                   $username,
                                   $password,
                                   array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));

    $consulta = "SELECT * FROM tbltoken WHERE idCliente = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bindValue(1,$idCliente);
    $stmt->execute();
    $num_token= $stmt->rowCount();

    if($num_token > 0){
        
       $array_tokens = array();
            
            while ($fila = $stmt->fetch(PDO::FETCH_ASSOC)){
                
                extract($fila);
                $token = array("idToken" => $idToken,
                               "token" => $token,
                               "idCliente" => $idCliente);
        
                array_push($array_tokens, $token);

            }
            $stmt=null;
            http_response_code(200);
            echo json_encode(array("tokens" => $array_tokens));
    }
    else{
        http_response_code(404);
    }
}
else{
    http_response_code(404);
}

?>