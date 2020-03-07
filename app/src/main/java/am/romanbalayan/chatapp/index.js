'use strict'


const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifs/{user_id}/{notification_id}').onWrite((data, context) => {

  const user_id = context.params.user_id;

  const notification = context.params.notification_id;

  console.log('We have a notification to send to : ', user_id);

  const fromUser = admin.database().ref(`/notifs/${user_id}/${notification}`).once('value');

  return fromUser.then(fromResults => {

    const from_Id = fromResults.val().from;

    console.log('Received a notif from  : ', from_Id);

	const userQuery = admin.database().ref(`Users/${from_Id}/name`).once('value');

	return userQuery.then(userResult => {
		const name = userResult.val();
		const deviceToken = admin.database().ref(`/Users/${user_id}/device token`).once('value');

    return deviceToken.then(result => {

      const token_id = result.val();

      const payload = {
        notification: {
          title: "Friend request",
          body: `${name} sent you friend request`,
          icon: "default",
	  click_action: "am.romanbalayan.chatapp_TARGET_NOTIFICATION"
        },
	data: {
		from_Id : from_Id
	}
      };

      return admin.messaging().sendToDevice(token_id, payload).then(response => {
        return console.log('send');
      });
    });
	});

   
  });
});