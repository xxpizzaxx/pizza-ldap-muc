import org.jivesoftware.whack.container.ServerContainer

/**
 * Created by Andi on 19/10/2015.
 */
class LdapMucService extends App {
  val sc = new ServerContainer()
  val muc = new LdapMuc("ldap-muc", "An LDAP-based MUC", true)
  sc.getManager.addComponent("ldap-muc", muc)
}
