USE authz;

// Create 'org' root NS
INSERT INTO ns (name,description,parent,scope,type)
  VALUES('org','Root Namespace','.',1,1);

INSERT INTO role(ns, name, perms, description)
  VALUES('org','admin',{'org.access|*|*'},'Org Admins');

INSERT INTO role(ns, name, perms, description)
  VALUES('org','owner',{'org.access|*|read,approve'},'Org Owners');

INSERT INTO perm(ns, type, instance, action, roles, description) 
  VALUES ('org','access','*','read,approve',{'org.owner'},'Org Read Access');

INSERT INTO perm(ns, type, instance, action, roles, description) 
  VALUES ('org','access','*','*',{'org.admin'},'Org Write Access');


// Create org.osaaf
INSERT INTO ns (name,description,parent,scope,type)
  VALUES('org.osaaf','OSAAF Namespace','org',2,2);

INSERT INTO role(ns, name, perms,description)
  VALUES('org.osaaf','admin',{'org.osaaf.access|*|*'},'OSAAF Admins');

INSERT INTO perm(ns, type, instance, action, roles,description) 
  VALUES ('org.osaaf','access','*','*',{'org.osaaf.admin'},'OSAAF Write Access');

INSERT INTO role(ns, name, perms,description)
  VALUES('org.osaaf','owner',{'org.osaaf.access|*|read,approve'},'OSAAF Owners');

INSERT INTO perm(ns, type, instance, action, roles,description) 
  VALUES ('org.osaaf','access','*','read,appove',{'org.osaaf.owner'},'OSAAF Read Access');

// Create org.osaaf.aaf
INSERT INTO ns (name,description,parent,scope,type)
  VALUES('org.osaaf.aaf','Application Authorization Framework','org.osaaf',3,3);

INSERT INTO role(ns, name, perms, description)
  VALUES('org.osaaf.aaf','admin',{'org.osaaf.aaf.access|*|*'},'AAF Admins');

INSERT INTO perm(ns, type, instance, action, roles, description) 
  VALUES ('org.osaaf.aaf','access','*','*',{'org.osaaf.aaf.admin'},'AAF Write Access');

INSERT INTO perm(ns, type, instance, action, roles, description) 
  VALUES ('org.osaaf.aaf','access','*','read,approve',{'org.osaaf.aaf.owner'},'AAF Read Access');

INSERT INTO role(ns, name, perms, description)
  VALUES('org.osaaf.aaf','owner',{'org.osaaf.aaf.access|*|read,approve'},'AAF Owners');

// OSAAF Root
INSERT INTO user_role(user,role,expires,ns,rname)
  VALUES ('aaf@aaf.osaaf.org','org.admin','2018-10-31','org','admin');

INSERT INTO user_role(user,role,expires,ns,rname)
  VALUES ('aaf@aaf.osaaf.org','org.osaaf.aaf.admin','2018-10-31','org.osaaf.aaf','admin');


