var functions = require('firebase-functions');
const admin = require('firebase-admin');
const firebase = require('firebase');
const algoliasearch = require('algoliasearch');
const dotenv = require('dotenv');

admin.initializeApp(functions.config().firebase);

const actionTypeNewLike = "new_like";
const actionTypeNewComment = "new_comment";
const actionTypeNewPost = "new_post";
const actionTypeNewFollow = "new_follower";
const notificationTitle = "Quotable";

const followingPosDbValue = "following_post";
const followingPosDbKey = "followingPostsIds";
const followingsDbKey = "followings";
const followersDbKey = "followers";
const followingDbKey = "follow";

const postsTopic = "postsTopic";

const ALGOLIA_ID = functions.config().algolia.app_id;
const ALGOLIA_ADMIN_KEY = functions.config().algolia.api_key;
const ALGOLIA_SEARCH_KEY = functions.config().algolia.search_key;

const ALGOLIA_INDEX_POSTS = 'posts';
const ALGOLIA_INDEX_PROFILES = 'profiles';
const client = algoliasearch(ALGOLIA_ID, ALGOLIA_ADMIN_KEY);

const postIndex = client.initIndex(ALGOLIA_INDEX_POSTS);
const profileIndex = client.initIndex(ALGOLIA_INDEX_PROFILES);

/*exports.onPostWrite = functions.database.ref('/posts/{postId}').onWrite((snapshot, context) => {
  const index = client.initIndex(ALGOLIA_INDEX_POSTS);
  //When the data is first created...
  if (snapshot.before.exists()) {
    const post = snapshot.before.val();

    post.objectID = context.params.postId;

    console.log('Updated post ', post.objectID);
    return index.saveObject(post);
  }
  //When data has been deleted...
  if (!snapshot.after.exists()) {
    const post = snapshot.before.val();

    post.objectID = context.params.postId;

    console.log('Deleted post ', post.objectID);
    return index.deleteObject(post.objectID);
  }

  if (!snapshot.exists()) {
    const post = snapshot.after.val();

    post.objectID = context.params.postId;

    console.log('Created post ', post.objectID);
    return index.saveObject(post);
  }
});*/

exports.onPostCreated = functions.database.ref('/posts/{postId}').onCreate((snapshot, context) => {
  const post = snapshot.val();
  post.objectID = context.params.postId;

  console.log('Added new post to Algolia: ', post.objectID);
  return postIndex.addObject(post);
});

exports.onPostUpdated = functions.database.ref('/posts/{postId}').onUpdate((snapshot, context) => {
  const post = snapshot.after.val();
  post.objectID = context.params.postId;

  console.log('Updated post ', post.objectID);
  return postIndex.saveObject(post);
});

exports.onPostDeleted = functions.database.ref('/posts/{postId}').onDelete((snapshot, context) => {
  const post = snapshot.val();
  post.objectID = context.params.postId;

  console.log('Delted post ', post.objectID);
  return postIndex.deleteObject(post.objectID);
});

exports.onProfileCreated = functions.database.ref('/profiles/{profileId}').onCreate((snapshot, context) => {
  const profile = snapshot.val();
  profile.objectID = context.params.profileId;

  console.log('New profile created ', profile.objectID);
  return profileIndex.addObject(profile);
});

exports.onProfileUpdate = functions.database.ref('/profiles/{profileId}').onUpdate((snapshot, context) => {
  const profile = snapshot.after.val();
  profile.objectID = context.params.profileId;

  console.log('Profile updated ', profile.objectID);
  return profileIndex.saveObject(profile);
});

exports.onProfileDeleted = functions.database.ref('/profiles/{profileId}').onDelete((snapshot, context) => {
  const profile = snapshot.val();
  profile.objectID = context.params.profileId;

  console.log('Profile delted ', profile.objectID);
});

exports.pushNotificationLikes = functions.database.ref('/post-likes/{postId}/{authorId}/{likeId}').onCreate((snap, context)  => {

    console.log('New like was added');

    const likeAuthorId = context.params.authorId;
    const postId = context.params.postId;

    // Get liked post.
    const getPostTask = admin.database().ref(`/posts/${postId}`).once('value');

    return getPostTask.then(post => {

        if (likeAuthorId === post.val().authorId) {
            console.log('User liked own post');
            return 'User liked own post';
        }

        // Get the list of device notification tokens.
        const getDeviceTokensTask = admin.database().ref(`/profiles/${post.val().authorId}/notificationTokens`).once('value');
        console.log('getDeviceTokensTask path: ', `/profiles/${post.val().authorId}/notificationTokens`);

        // Get like author.
        const getLikeAuthorProfileTask = admin.database().ref(`/profiles/${likeAuthorId}`).once('value');

        return Promise.all([getDeviceTokensTask, getLikeAuthorProfileTask]).then(results => {
            const tokensSnapshot = results[0];
            const likeAuthorProfile = results[1].val();

            // Check if there are any device tokens.
            if (!tokensSnapshot.hasChildren()) {
                return console.log('There are no notification tokens to send to.');
            }

            console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
            console.log('Fetched like Author profile', likeAuthorProfile);

            // Create a notification
            const payload = {
                data: {
                    actionType: actionTypeNewLike,
                    title: notificationTitle,
                    body: `${likeAuthorProfile.username} liked your post`,
                    icon: likeAuthorProfile.photoUrl,
                    postId: postId,
                },
            };

            // Listing all tokens.
            const tokens = Object.keys(tokensSnapshot.val());
            console.log('tokens:', tokens[0]);

            // Send notifications to all tokens.
            return admin.messaging().sendToDevice(tokens, payload).then(response => {
                // For each message check if there was an error.
                const tokensToRemove = [];
                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Failure sending notification to', tokens[index], error);
                        // Cleanup the tokens who are not registered anymore.
                        if (error.code === 'messaging/invalid-registration-token' ||
                            error.code === 'messaging/registration-token-not-registered') {
                            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                        }
                    }
                });
                return Promise.all(tokensToRemove);
            });
        }).catch((fallback) => {
            console.error('Failure getPostTask', fallback);
        });
    }).catch((fallback) => {
        console.error('Failure getPostTask', fallback);
    })
});

exports.pushNotificationComments = functions.database.ref('/post-comments/{postId}/{commentId}').onCreate((snap, context) => {

    const commentId = context.params.commentId;
    const postId = context.params.postId;
    const comment = snap.val();

    console.log('New comment was added, id: ', postId);

    // Get the commented post .
    const getPostTask = admin.database().ref(`/posts/${postId}`).once('value');

    return getPostTask.then(post => {

        // Get the list of device notification tokens.
        const getDeviceTokensTask = admin.database().ref(`/profiles/${post.val().authorId}/notificationTokens`).once('value');
        console.log('getDeviceTokensTask path: ', `/profiles/${post.val().authorId}/notificationTokens`);

        // Get post author.
        const getCommentAuthorProfileTask = admin.database().ref(`/profiles/${comment.authorId}`).once('value');
        console.log('getCommentAuthorProfileTask path: ', `/profiles/${comment.authorId}`);

        return Promise.all([getDeviceTokensTask, getCommentAuthorProfileTask]).then(results => {
            const tokensSnapshot = results[0];
            const commentAuthorProfile = results[1].val();

            if (commentAuthorProfile.id === post.val().authorId) {
                console.log('User commented own post');
                return 'User commented own post';
            }

            // Check if there are any device tokens.
            if (!tokensSnapshot.hasChildren()) {
                console.log('There are no notification tokens to send to.');
                return 'There are no notification tokens to send to.';
            }

            console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');

            // Create a notification
            const payload = {
                data: {
                    actionType: actionTypeNewComment,
                    title: notificationTitle,
                    body: `${commentAuthorProfile.username} commented your post`,
                    //icon: post.val().photoUrl,
                    postId: postId,
                },
            };

            // Listing all tokens.
            const tokens = Object.keys(tokensSnapshot.val());
            console.log('tokens:', tokens[0]);

            // Send notifications to all tokens.
            return admin.messaging().sendToDevice(tokens, payload).then(response => {
                // For each message check if there was an error.
                const tokensToRemove = [];
                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Failure sending notification to', tokens[index], error);
                        // Cleanup the tokens who are not registered anymore.
                        if (error.code === 'messaging/invalid-registration-token' ||
                            error.code === 'messaging/registration-token-not-registered') {
                            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                        }
                    }
                });
                return Promise.all(tokensToRemove);
            });
        }).catch((fallback) => {
            console.error('Failure getPostTask', fallback);
        });
    }).catch((fallback) => {
        console.error('Failure getPostTask', fallback);
    })
});

exports.pushNotificationNewFollwer = functions.database.ref('/follow/{profileId}/followings/{followerId}').onCreate((snap, context) => {
  const profileId = context.params.profileId;
  const followerId = context.params.followerId;

  // Get follower profile
  const getFollowerProfileTask = admin.database().ref(`/profiles/${followerId}`).once('value');

  return getFollowerProfileTask.then(profile => {
    const getDeviceTokensTask = admin.database().ref(`/profiles/${profile.val().id}/notificationTokens`).once('value');
    console.log('getDeviceTokensTask path: ', `/profiles/${profile.val().id}/notificationTokens`);

    //Get destination profile
    const getProfileTask = admin.database().ref(`/profiles/${profileId}`).once('value');

    return Promise.all([getDeviceTokensTask, getProfileTask]).then(results => {
      const tokensSnapshot = results[0];
      const profileObj = results[1].val();

      // Check if there are any device tokens
      if (!tokensSnapshot.hasChildren()) {
        return console.log('There are no tokens to send notifications to.');
      }

      console.log('There are ', tokensSnapshot.numChildren(), ' tokens to send notifications to.');
      console.log('Fetched follower profile', profileObj);

      // Create a notification
      const payload = {
        data: {
          actionType: actionTypeNewFollow,
          title: notificationTitle,
          profileId: profileObj.id,
          body: `${profileObj.username} is now following you!`,
          icon: profileObj.photoUrl,
        },
      };

      // Listing all tokens
      const tokens = Object.keys(tokensSnapshot.val());
      console.log('tokens:', tokens[0]);

      //send notification to all tokens
      return admin.messaging().sendToDevice(tokens, payload).then(response => {
        // For each message, check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failed to send notifications to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore
            if (error.code === 'messaging/invalid-registration-token' || error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      });

    }).catch((fallback) => {
      console.error('Failure getProfileTask', fallback);
    });
  }).catch((fallback) => {
    console.error('Failure getFollowerProfileTask', fallback);
  })
});

exports.pushNotificationNewPost = functions.database.ref('/posts/{postId}').onCreate((snap, context) => {
    const postId = context.params.postId;

    console.log('New post was created');

    // Get post authorID.
    const getAuthorIdTask = admin.database().ref(`/posts/${postId}/authorId`).once('value');

    return getAuthorIdTask.then(authorId => {

        console.log('post author id', authorId.val());

        // Create a notification
        const payload = {
            data: {
                actionType: actionTypeNewPost,
                postId: postId,
                authorId: authorId.val(),
            },
        };

        // Send a message to devices subscribed to the provided topic.
        return admin.messaging().sendToTopic(postsTopic, payload).then(response => {
                // See the MessagingTopicResponse reference documentation for the
                // contents of response.
                console.log("Successfully sent info about new post :", response);
                return response;
            })
            .catch(error => {
                console.log("Error sending info about new post:", error);
            });
    }).catch(fallback => {
        console.error('Failure getPostTask', fallback);
    });

});

exports.addNewPostToFollowers = functions.database.ref('/posts/{postId}').onCreate((snap, context) => {
    const postId = context.params.postId;

    console.log('New post was created');

    // Get post authorID.
    const getAuthorIdTask = admin.database().ref(`/posts/${postId}/authorId`).once('value');

    return getAuthorIdTask.then(authorId => {

        console.log('post author id', authorId.val());

        // Get followers ids.
        return admin.database().ref().child(followingDbKey).child(authorId.val()).child(followersDbKey).once('value', function(snapshot) {
            snapshot.forEach(function (childSnapshot) {
                let followerId = childSnapshot.val().profileId;
                console.log('setNewPostValuesToFollower', "followerId:", followerId, "postId:", postId);

                admin.database().ref().child(followingPosDbKey).child(followerId).child(postId).set({
                    postId:postId
                });
            });
        }).catch(fallback => {
            console.error('Failure get followers ids', fallback);
        });

    }).catch(fallback => {
        console.error('Failure getPostTask', fallback);
    });

});

exports.removePostFromFollowingList = functions.database.ref('/posts/{postId}').onDelete((snap, context) => {
    const postId = context.params.postId;
    const authorId = snap.val().authorId;

    // Get followers ids.
    return admin.database().ref().child(followingDbKey).child(authorId).child(followersDbKey).once('value', function(snapshot) {
        snapshot.forEach(function (childSnapshot) {
            let followerId = childSnapshot.val().profileId;
            admin.database().ref().child(followingPosDbKey).child(followerId).child(postId).remove();
            console.log('remove post if from following list for followerId:', followerId, "postId:", postId);
        });
    }).catch(fallback => {
        console.error('Failure get followers ids', fallback);
    });
});
