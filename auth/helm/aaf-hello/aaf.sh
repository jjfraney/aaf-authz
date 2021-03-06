. ../../docker/aaf.props
IMAGE=onap/aaf/aaf_agent:$VERSION

kubectl -n onap run -it --rm aaf-agent-$USER --image=$IMAGE --overrides='
{
    "spec": {
        "containers": [
            {
                "name": "aaf-agent-'$USER'",
		"image": "'$IMAGE'",
                "imagePullPolicy": "IfNotPresent",
		"command": [
                   "bash", 
		   "-c",
                   "/opt/app/aaf_config/bin/agent.sh && cd /opt/app/osaaf/local && exec bash"
                 ],
                "env": [
                   {
                     "name": "APP_FQI",
                     "value": "aaf@aaf.osaaf.org"
                   },{
                     "name": "DEPLOY_FQI",
                     "value": "deployer@people.osaaf.org"
                   },{
                     "name": "DEPLOY_PASSWORD",
                     "value": "demo123456!"
                   },{
                     "name": "aaf_locate_url",
                     "value": "https://aaf-locate.onap:8095"
                   },{
                     "name": "aaf_locator_container",
                     "value": "helm"
                   },{
                     "name": "aaf_locator_container_ns",
                     "value": "onap"
                   },{
                     "name": "aaf_locator_public_fqdn",
                     "value": "aaf.osaaf.org"
                   },{
                     "name": "aaf_locator_fqdn",
                     "value": "aaf-hello"
                   },{
                     "name": "cadi_latitude",
                     "value": "'$LATITUDE'"
                   },{
                     "name": "cadi_longitude",
                     "value": "'$LONGITUDE'"
                   }
                ],
                "stdin": true,
                "stdinOnce": true,
                "tty": true,
                "volumeMounts": [
                    {
                        "mountPath": "/opt/app/osaaf",
                        "name": "aaf-hello-vol"
                    }
                ]
            }
        ],
      "volumes": [
            {
                "name": "aaf-hello-vol",
                "persistentVolumeClaim": {
                    "claimName": "aaf-hello-pvc"
                }
            }
        ]
   }
}
' --restart=Never  -- bash 
