/**
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 * ===========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END====================================================
 *
 */

package org.onap.aaf.cadi.configure;

import java.io.File;

import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.aaf.Defaults;
import org.onap.aaf.cadi.config.Config;
import org.onap.aaf.cadi.util.Chmod;
import org.onap.aaf.misc.env.Trans;
import org.onap.aaf.misc.env.util.Chrono;
import org.onap.aaf.misc.env.util.Split;

import certman.v1_0.Artifacts.Artifact;
import certman.v1_0.CertInfo;

public class PlaceArtifactScripts extends ArtifactDir {
    @Override
    public boolean _place(Trans trans, CertInfo certInfo, Artifact arti) throws CadiException {
        try {
            // Setup check.sh script
            String filename = arti.getNs()+".check.sh";
            File f1 = new File(dir,filename);
            String email = arti.getNotification() + '\n';
            if (email.startsWith("mailto:")) {
                email=email.substring(7);
            }  else {
                email=arti.getOsUser() + '\n';
            }

            StringBuilder classpath = new StringBuilder();
            boolean first = true;
            for (String pth : Split.split(File.pathSeparatorChar, System.getProperty("java.class.path"))) {
                if (first) {
                    first=false;
                } else {
                    classpath.append(File.pathSeparatorChar);
                }
                File f = new File(pth);
                classpath.append(f.getCanonicalPath().replaceAll("[0-9]+\\.[0-9]+\\.[0-9]+",Defaults.AAF_VERSION+".*"));
            }

            write(f1,Chmod.to644,
                    "#!/bin/bash " + f1.getCanonicalPath()+'\n',
                    "# Certificate Manager Check Script\n",
                    "# Check on Certificate, and renew if needed.\n",
                    "# Generated by Certificate Manager " + Chrono.timeStamp()+'\n',
                    "#   by Deployer " + trans.getProperty(Config.AAF_APPID,"") + '\n',
                    "#\n",
                    "DIR="+arti.getDir()+'\n',
                    "APP_ID=" + arti.getMechid() + '\n',
                    "FQDN=" + arti.getMachine()+ '\n',
                    "APP="+arti.getNs()+'\n',
                    "EMAIL="+email+ '\n',
                    "JAR=\""+classpath.toString()+"\"\n",
                    "if [ -z \"$JAVA_HOME\" ]; then \n",
                    "  JAVA=\""+javaHome() + "/bin/" +"java\"\n",
                    "else\n",
                    "  JAVA=\"$JAVA_HOME/bin/java\"\n",
                    "fi\n",
                    checkScript
                    );

            // Setup check.sh script
            File f2 = new File(dir,arti.getNs()+".crontab.sh");
            write(f2,Chmod.to644,
                    "#!/bin/bash " + f2.getCanonicalPath()+'\n',
                    "# Certificate Manager Crontab Loading Script\n",
                    "# Add/Update a Crontab entry, that adds a check on Certificate Manager generated Certificate nightly.\n",
                    "# Generated by Certificate Manager " + Chrono.timeStamp()+'\n',
                    "TFILE=\"/tmp/cmcron$$.temp\"\n",
                    "DIR=\""+arti.getDir()+"\"\n",
                    "CF=\""+arti.getNs()+" Certificate Check Script\"\n",
                    "SCRIPT=\""+f1.getCanonicalPath()+"\"\n",
                    cronScript
                    );

        } catch (Exception e) {
            throw new CadiException(e);
        }
        return true;
    }

    /**
     * Note: java.home gets Absolute Path of Java, where we probably want soft links from
     * JAVA_HOME
     * @return
     */
    private final static String javaHome() {
        String rc = System.getenv("JAVA_HOME");
        return rc==null?System.getProperty("java.home"):rc;
    }
    private final static String checkScript =
            "function mailit {\n" +
            "  if [ -e /bin/mail ]; then\n" +
            "     MAILER=/bin/mail\n" +
            "  elif [ -e /usr/bin/mail ]; then \n" +
            "     MAILER=/usr/bin/mail\n" +
            "  else \n" +
            "     MAILER=\"\"\n" +
            "  fi\n" +
            " if [ \"$MAILER\" = \"\" ]; then\n" +
            "    printf \"$*\"\n" +
            " else \n" +
            "    printf \"$*\" | $MAILER -s \"AAF Certman Notification for `uname -n`\" $EMAIL\n"+
            " fi\n" +
            "}\n\n" +
            "> $DIR/$APP.msg\n\n" +
            "$JAVA -jar $JAR check $APP_ID $FQDN cadi_prop_files=$DIR/$APP.props 2>  $DIR/$APP.STDERR > $DIR/$APP.STDOUT\n" +
            "case \"$?\" in\n" +
            "  0)\n" +
            "    # Note: Validation will be mailed only the first day after any modification\n" +
            "    if [ \"`find $DIR -mtime 0 -name $APP.check.sh`\" != \"\" ] ; then\n" +
            "       mailit `echo \"Certficate Validated:\\n\\n\" | cat - $DIR/$APP.msg`\n" +
            "    else\n" +
            "       cat $DIR/$APP.msg\n" +
            "    fi\n" +
            "    ;;\n" +
            "  1) mailit \"Error with Certificate Check:\\\\n\\\\nCheck logs $DIR/$APP.STDOUT and $DIR/$APP.STDERR on `uname -n`\"\n" +
            "    ;;\n" +
            "  2) mailit `echo \"Certificate Check Error\\\\n\\\\n\" | cat - $DIR/$APP.msg`\n" +
            "    ;;\n" +
            "  10) mailit `echo \"Certificate Replaced\\\\n\\\\n\" | cat - $DIR/$APP.msg`\n" +
            "      if [ -e $DIR/$APP.restart.sh ]; then\n" +
            "        # Note: it is THIS SCRIPT'S RESPONSIBILITY to notify upon success or failure as necessary!!\n" +
            "        /bin/sh $DIR/$APP.restart.sh\n" +
            "      fi\n" +
            "    ;;\n" +
            "  *) mailit `echo \"Unknown Error code for CM Agent\\\\n\\\\n\" | cat - $DIR/$APP.msg`\n" +
            "    ;;\n" +
            " esac\n\n" +
            " # Note: make sure to cover this sripts' exit Code\n";

    private final static String cronScript =
            "crontab -l | sed -n \"/#### BEGIN $CF/,/END $CF ####/!p\" > $TFILE\n" +
            "# Note: Randomize Minutes (0-60) and hours (1-4)\n" +
            "echo \"#### BEGIN $CF ####\" >> $TFILE\n" +
            "echo \"$(( $RANDOM % 60)) $(( $(( $RANDOM % 3 )) + 1 )) * * * /bin/bash $SCRIPT " +
                ">> $DIR/cronlog 2>&1 \" >> $TFILE\n" +
            "echo \"#### END $CF ####\" >> $TFILE\n" +
            "crontab $TFILE\n" +
            "rm $TFILE\n";
}



