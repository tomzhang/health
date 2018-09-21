package test;


public class TigaseTest {

	public static void main(String[] args) throws Exception {
		// String roomName = "大房子";
		// XMPPConnection connection = getConnection("luorc", "123456");
		// MultiUserChat muc = new MultiUserChat(connection, "aabbcdd" + "@muc."
		// + connection.getServiceName());
		// muc.create(roomName);
		//
		// // 获得聊天室的配置表单
		// Form form = muc.getConfigurationForm();
		// // 根据原始表单创建一个要提交的新表单。
		// Form submitForm = form.createAnswerForm();
		// // 向要提交的表单添加默认答复
		// Iterator<FormField> fields = form.getFields();
		// while (fields.hasNext()) {
		// FormField field = fields.next();
		// if (!FormField.TYPE_HIDDEN.equals(field.getType()) &&
		// field.getVariable() != null) {
		// // 设置默认值作为答复
		// submitForm.setDefaultAnswer(field.getVariable());
		// }
		// }

		// muc#roomconfig_roomname 房间名称
		// muc#roomconfig_roomdesc 房间描述
		// muc#roomconfig_persistentroom 房间是持久的
		// muc#roomconfig_publicroom 公开的，允许被搜索到
		// muc#roomconfig_moderatedroom 房间是临时的
		// muc#roomconfig_membersonly 房间仅对成员开放
		// muc#roomconfig_passwordprotectedroom 需要密码才能进入的房间
		// muc#roomconfig_roomsecret 设置房间密码
		// muc#roomconfig_anonymity 匿名的房间
		// muc#roomconfig_changesubject 允许占有者更改主题
		// muc#roomconfig_enablelogging 登陆房间对话
		// logging_format
		// muc#maxhistoryfetch

		// submitForm.setAnswer("muc#roomconfig_roomname", roomName);
		// submitForm.setAnswer("muc#roomconfig_roomdesc", roomName);
		// submitForm.setAnswer("muc#roomconfig_persistentroom", true);
		// submitForm.setAnswer("muc#roomconfig_publicroom", true);
		// submitForm.setAnswer("muc#roomconfig_enablelogging", true);
		// muc.sendConfigurationForm(submitForm);
		// connection.disconnect();

		// XMPPConnection connection = getConnection("taowc", "123456");
		//
		// connection.addPacketListener(new PacketListener() {
		//
		// @Override
		// public void processPacket(Packet packet) {
		// System.out.println("AAA   " + packet.toXML());
		// }
		// }, null);
		//
		// // Chat chat =
		// // connection.getChatManager().createChat("10000001@www.youjob.co",
		// new
		// // MessageListener() {
		// //
		// // @Override
		// // public void processMessage(Chat chat, Message message) {
		// // // System.out.println(message.toXML());
		// // }
		// // });
		// // chat.sendMessage("哈喽哈喽...");
		//
		// MultiUserChat muc = new MultiUserChat(connection,
		// "54d49a846aa3b6ef83b60a46" + "@muc." + connection.getServiceName());
		// // muc.join("哈喽哈喽");
		// muc.sendMessage("来个回执把");
		// muc.addMessageListener(new PacketListener() {
		//
		// @Override
		// public void processPacket(Packet packet) {
		// // System.out.println(packet.toXML());
		// }
		// });
	}


}
