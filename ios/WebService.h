//
//  Handles all interaction with the servers
//  WebService.h
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface WebService : NSObject

#pragma mark Returns defaultInstance
// returns a default instance of WebService to implement singleton
// in other words only one instance of WebService object exists in the application
+ (WebService *)defaultInstance;

#pragma mark Create User
// web service call to create user
- (NSDictionary *)createUserWithFirstName:(NSString *)firstName andLastName:(NSString *)lastName andEmail:(NSString *)email andRoleOrDescription:(NSString *)roleOrDescription andReferredBy:(NSString *)referredBy andProfilePictureExtension:(NSString *)profilePictureExtension;

#pragma mark Change password for a user
// web service call to change password for a user
- (NSDictionary *)changePasswordForUserWithEmail:(NSString *)email password:(NSString *)password andTemporaryPassword:(NSString *)temporaryPassword;

#pragma mark Authenticate User
// web service call to authenticate a user
- (NSMutableDictionary *)authenticateUserWithEmail:(NSString *)email andPassword:(NSString *)password;

#pragma mark Get already authenticated user's properties
// web service call to get already authenticated user's properties
- (NSMutableDictionary *)returnPropertiesOfAuthenticatedUserWithEmail:(NSString *)email;

#pragma mark Update user's profile picture
// web service call to update user's profile picture
- (NSDictionary *)updateUsersProfilePictureWithUsername: (NSString *)username andProfilePictureData:(NSData *)profilePictureData andProfilePictureExtension:(NSString *)profilePictureExtension;

#pragma mark Download AttachmentsData
// web service call to download attachmentsData
- (id)returnAttachmentsDataWithSearchTerm:(NSString *)searchTerm;

#pragma mark Update numberOfViews on Attachment with id=aid
// web service call to update numberOfViews on Attachment with id=aid
- (void)updateNumberOfViewsForAttachmentWithId:(NSString *)aid;

#pragma mark Upload an attachment
// web service call to upload an attachment (audio or video file)
- (NSDictionary *)uploadAttachmentWithUserEmail:(NSString *)email andFileName:(NSString *)fileName andFileDescription:(NSString *)fileDescription andFilePoster:(NSData *)filePosterData andFilePosterURL:(NSString *)filePosterURL andFileToBeUploaded:(NSData *)fileToBeUploadedData andFileNameOnSystem:(NSString *)fileNameOnSystem andFilePosterExtension:(NSString *)filePosterExtension andFileToBeUploadedExtension:(NSString *)fileToBeUploadedExtension andFileToBeUploadedType:(NSString *)fileToBeUploadedType andFileHashTags:(NSString *)fileHashTags;

#pragma mark Get trending hashTags
// web service call to get trending hashTags
- (NSDictionary *)returnTrendingHashTags;

#pragma mark Append data to attachment
// web service call to append data to attachment
- (NSDictionary *)appendDataToAttachmentWithId:(NSString *)attachmentId andFileNameOnSystem:(NSString *)fileNameOnSystem andFileData:(NSData *)fileData andFileToBeUploadedExtension:(NSString *)fileToBeUploadedExtension andFileToBeUploadedType:(NSString *)fileToBeUploadedType;

#pragma mark Forgot Password?
// web service call when user has forgotten his password and clicks on "Forgot Password?"
- (NSDictionary *)forgotPasswordForEmail:(NSString *)email;

#pragma mark Send message to talent or in other words create conversation
// web service call to send message to talent or in other words create conversation
- (NSDictionary *)sendMessage:(NSString *)messageText toTalent:(NSString *)receiver fromUserWithEmail:(NSString *)sender;

# pragma mark Append data to Attachment's poster
// web service call to append data to attachment's poster
- (NSDictionary *)appendPosterDataToAttachmentWithId:(NSString *)attachmentId andFilePosterData:(NSData *)filePosterData andFilePosterExtension:(NSString *)filePosterExtension andFileNameOnSystem:(NSString *) fileNameOnSystem;

@end
