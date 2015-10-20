import moe.pizza.auth.AuthConfig
import moe.pizza.auth.ldap.LDAPTools
import org.jivesoftware.openfire.muc.spi.MultiUserChatServiceImpl
import org.xmpp.packet.PacketError.Condition
import org.xmpp.packet.{Packet, IQ}

/**
 * Implementation of a MUC server with LDAP support, based on the one from Openfire
 * @param subdomain
 * @param description
 * @param isHidden
 */
class LdapMuc(conf: AuthConfig, subdomain: String, description: String, isHidden: Boolean) extends MultiUserChatServiceImpl(subdomain, description, isHidden) {

  val ldaptools = new LDAPTools(conf)

  /**
   * Utility function to fail this IQ request
   * @param iq
   */
  def fail(iq: IQ): Unit = {
      val reply = IQ.createResultIQ(iq)
      reply.setChildElement(iq.getChildElement.createCopy())
      reply.setError(Condition.not_allowed)
      this.router.route(reply)
  }

  /**
   * Overriden copy of processPacket so that we can screen for registration requests and check them against LDAP,
   * denying them if we believe the user shouldn't be allowed in.
   * @param packet
   */
  override def processPacket(packet: Packet): Unit = {
    packet match {
      /*
        We only want to intercept packets which are valid register requests, so we can check them against LDAP and screen them
      */
      case iq: IQ if iq.getType!=IQ.Type.error &&
                     iq.getChildElement != null &&
                     iq.getChildElement.getNamespaceURI == "jabber:iq:register" &&
                     Option(iq.getTo.getNode).map(this.getChatRoom).isDefined // maybe remove this? we may want to intercept even if the room doesn't exist yet
                      =>
        val target = iq.getTo.getNode // should be the room name
        ldaptools.loadUser(iq.getFrom.getNode) match {
          case Some(user) =>
            user.authGroup.contains(target) match {
              case true => super.processPacket(packet) // if we think they should be allowed in, let the normal server take it from here
              case false => fail(iq) // if they aren't in the group in LDAP, fail the request
            }
          case None => fail(iq) // if the user doesn't exist in LDAP, fail the request
        }
      case _ => super.processPacket(packet) // fall through everything else to the normal server
    }
  }

}
