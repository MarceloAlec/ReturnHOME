<?php


class Connection extends PDO
{
    /*CREDENCIALES DE ACCESO A LA BASE DE DATOS */
    private $hostname = 'localhost';
    private $hostuser = 'root';
    private $hostpass = '1998*';
    private $dbname = 'dbreturnhome';

    public function __construct(){
        try
        {
            /* EL CONSTRUCTOR DE PDO RECIBE LAS CREDENCIALES PARA LA 
            CONEXION DE LA BASE DE DATOS */
            parent::__construct('mysql:host=' . $this->hostname . ';dbname=' . $this->dbname . ';charset=utf8', 
                                $this->hostuser, 
                                $this->hostpass
                                ,array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
        }catch(PDOException $e){
            echo 'ERROR: ' . $e->getMessage();
            exit;
        }
    }


}

?>