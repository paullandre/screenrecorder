<?php
global $file;
$count = 0;
$num = 0;
$audio = "audio";
$video = "video";
$pelikula = "pelikula";

if(is_dir($video))
{
    $extensions = scandir($video);
    $counter = count($extensions);
    for ($i=0; $i<$counter; $i++)
    {
        if(!is_null($extensions[$i]))
        {
            if(endsWith($extensions[$i], ".avi", true))
            {
                $test = end(explode('.', $extensions[$i]));
                die($test);
                $name = substr($extensions[$i], -8, 4);
                $theSeconds = substr($name,-2);
                $theMinutes = substr($name, -4 ,2);
                $vlsService = substr_replace($extensions[$i], null, -4);                
                die($vlsService);
                //die($name. " " .$theSeconds. " " .$theMinutes. " " .$vlsService);

                $source = "/db/dist/video/".$theSeconds."/".$theMinutes."/".$vlsService.".wav";
                $target = "/db/dist/FTP/".$vlsService.".wav";
                die($extensions[$i] . " " . $target);
                $connection = ssh2_connect("10.100.1.180", 22) or die("Could not connect to server");
                ssh2_auth_password($connection, "webuser","b0tand1ng") or die("Problem with username/password!");
                $scpResult = ssh2_scp_recv($connection1, $source, $target);
                if(!$scpResult) continue;

                die();
//                $connection = ssh2_connect('10.100.1.203', 22) or die("Could not connect 203");
//                ssh2_auth_password($connection1, "ftpvoip","ftpv0ip") or die("Could not login 203");
//                $scpResult = ssh2_scp_recv($connection1, $source, $target) or die("Could not copy 203");

                exec("ffmpeg -i /db/dist/audio/".$vlsService.".wav -i /db/dist/video/".$vlsService.".avi -acodec libfaac -ab 16000 -pass 1 -vcodec libx264 -vpre medium -b 80000 -y /db/dist/output/OUTPUT.mp4");
                exec("rm -f /db/dist/audio/0237.wav");
            }
            else
            {
                echo("Invalid file found\n");
            }
        }
        else
        {
            echo("No file found\n");
        }
    }
}
die();

try
{
    exec("ffmpeg -i /db/dist/audio/ES_E386200_A384915_I39699_C-5264_D20110513_T202343.wav -i /db/dist/video/ES_E386200_A384915_I39699_C-5264_D20110513_T202343.avi -acodec libfaac -ab 16000 -pass 1 -vcodec libx264 -vpre medium -b 80000 -y /db/dist/output/ES_E386200_A384915_I39699_C-5264_D20110513_T202343.mp4");
    //exec("rm -f 0237.avi");
    //echo("\n\nDone\n");
}
catch(Exception $e)
{
    $e->getMessage();
}

function endsWith($haystack,$needle,$case=true)
{
    if($case)
    {
        return (strcmp(substr($haystack, strlen($haystack) - strlen($needle)),$needle)===0);
    }
    return (strcasecmp(substr($haystack, strlen($haystack) - strlen($needle)),$needle)===0);
}
?> 