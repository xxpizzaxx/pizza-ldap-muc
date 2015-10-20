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

  def fail(iq: IQ): Unit = {
      val reply = IQ.createResultIQ(iq)
      reply.setChildElement(iq.getChildElement.createCopy())
      reply.setError(Condition.not_allowed)
      this.router.route(reply)
  }

  override def processPacket(packet: Packet): Unit = {
    packet match {
      case iq: IQ if iq.getType!=IQ.Type.error &&
                     iq.getChildElement != null &&
                     iq.getChildElement.getNamespaceURI == "jabber:iq:register" &&
                     Option(iq.getTo.getNode).map(this.getChatRoom).isDefined

                      =>

        val target = iq.getTo.getNode
        ldaptools.loadUser(iq.getFrom.getNode) match {
          case Some(user) =>
            user.authGroup.contains(target) match {
              case true => super.processPacket(packet)
              case false => fail(iq)
            }
          case None => fail(iq)
        }
      case _ => super.processPacket(packet)
    }
  }

}
