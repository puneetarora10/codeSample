//
//  HelperService.h
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HelperService : NSObject

#pragma mark Returns defaultInstance
// returns a default instance of HelperService to implement singleton
// in other words only one instance of HelperService object exists in the application
+ (HelperService *)defaultInstance;

#pragma mark saveUsersData
// saves users' data after user has successfully loggedIn in to NSUserDefaults
// usersData = {"profilePictureURLString":"", "fullName": "", "email": "", "userSince":""}
- (void)saveUsersData:(NSMutableDictionary *) usersData;

#pragma mark updates NSUserDefaults' profilePictureURLString
// updates NSUserDefaults' profilePictureURLString
- (void)updateProfilePictureURLString:(NSString *)profilePictureURLString;

#pragma mark clears NSUserDefaults for this app
// called when user logs out - clears NSUserDefaults for this app
- (void)clearNSUserDefaults;

#pragma mark shows activity indicator on a view
// shows activity indicator on a view
- (void)showActivityIndicatorOnView:(UIView *)view withFrame:(CGRect)frame;

#pragma mark hides activity indicator (already existing) on a view
// hides activity indicator (already existing) on a view
- (void)hideActivityIndicator;

#pragma mark makes "Squared" UIButton look like "Rounded Rectangle"
// makes "Squared" UIButton look like "Rounded Rectangle"
- (void)changeSquaredUIButtonToRoundedRect:(UIButton *)button;

#pragma mark returns YES if device is an IPAD
// returns YES if device is an IPAD
- (BOOL)checkIfCurrentDeviceIsAnIPAD;

#pragma mark returns YES if user is logged in using NSUserDefaults
// returns YES if user is logged in using NSUserDefaults
- (BOOL)checkIfUserIsLoggedIn;

#pragma mark returns email of logged in user using NSUserDefaults
// returns email of logged in user using NSUserDefaults
- (NSString *)returnLoggedInUsersEmail;

#pragma mark returns attachmentsCount of logged in user using NSUserDefaults
// returns attachmentsCount of logged in user using NSUserDefaults
- (NSString *)returnLoggedInUsersAttachmentsCount;

#pragma mark checkIfLoggedInUserIsAnArtist
// returns YES if logged in user is an "ARTIST" or in other terms has the userRole = "ARTIST"
- (BOOL)checkIfLoggedInUserIsAnArtist;

#pragma mark checkIfLoggedInUserIsAnAdministrator
// returns YES if logged in user is an "ADMINISTRATOR" or in other terms has the userRole = "ADMINISTRATOR"
- (BOOL)checkIfLoggedInUserIsAnAdministrator;

#pragma mark checkIfLoggedInUserIsATalent
// returns YES if logged in user is a "TALENT" or in other terms has the userRole = "ARTIST" or "WRITER" OR "PRODUCER"
- (BOOL)checkIfLoggedInUserIsATalent;

#pragma mark checkIfLoggedInUserIsAnAandR
// returns YES if logged in user is an "AandR" or in other terms has the userRole = "AANDR"
- (BOOL)checkIfLoggedInUserIsAnAandR;

#pragma mark redirects to MeViewController
// redirects to MeViewController
- (void)redirectToMeViewController:(UIViewController *)currentViewController;

#pragma mark redirects to HomeViewController
// redirects to HomeViewController and sets HomeViewController's searchBar's text
- (void)redirectToHomeViewController:(UIViewController *)currentViewController withSearchTerm:(NSString *)searchTerm;

#pragma mark shows a generic alertView
// show an alertView with a title, a message and a button with buttonTitle
- (void)showAlertViewWithTitle:(NSString *)title andMessage:(NSString *)message andACancelButtonWithTitle:(NSString *)buttonTitle;

#pragma mark GotSigned access to Media/ Photos alertView
// show an alertView for giving GotSigned access to Media/ Photos
- (void)showAlertViewForGivingMediaAccess;

#pragma mark returns size of file in MB
// returns size of file in MB
- (long)returnSizeOfFileInMBWithMediaURL:(NSURL *)mediaURL;

#pragma mark checkIfAppHasAccessToMedia
// returns YES if app has access to Media (PHOTOS)
- (BOOL)checkIfAppHasAccessToMedia;

#pragma mark checkIfEmailIsValid
// returns YES if the email address looks valid
- (BOOL)checkIfEmailIsValid:(NSString *)email;

#pragma mark returnServersURLString
// returns server's URL string
- (NSMutableString *)returnServersURLString;

#pragma mark appends generic errorMessage
// appends generic errorMessage
- (void)appendGenericErrorMessageTo:(NSMutableDictionary *)appendGenericErrorMessageToIt;

#pragma mark appends (key/value) (FORM)
// appends (key/value) to data in the format used by browsers to submit forms
- (void)appendFormDataWithKey:(NSString *)key andValue:(NSString *)value andBoundary:(NSString *)boundary toData:(NSMutableData *)data;

#pragma mark append file to data to submit forms
// appends file to data in the format used by browsers to submit forms
- (void)appendFormDataWithKey:(NSString *)key andFileName:(NSString *)fileName andContentType:(NSString *)contentType andFileToBeUploadedData:(NSData *)fileToBeUploadedData andBoundary:(NSString *)boundary toData:(NSMutableData *)data;

#pragma mark saveFileUploadInProgress in to NSUserDefaults
// saves fileUploadInProgress in to NSUserDefaults
- (void)saveFileUploadInProgress:(BOOL)fileUploadInProgress;

#pragma mark saveFileUploadFailed in to NSUserDefaults
// saves fileUploadFailed in to NSUserDefaults
- (void)saveFileUploadFailed:(BOOL)fileUploadFailed;

#pragma mark returns fileUploadInProgress using NSUserDefaults
// returns fileUploadInProgress using NSUserDefaults
- (BOOL)returnFileUploadInProgess;

#pragma mark returns fileUploadFailed using NSUserDefaults
// returns fileUploadFailed using NSUserDefaults
- (BOOL)returnFileUploadFailed;

#pragma mark saves mediaAccessRequested in to NSUserDefaults
// saves mediaAccessRequested in to NSUserDefaults
- (void)saveMediaAccessRequested;

#pragma mark returns mediaAccessRequested using NSUserDefaults
// returns mediaAccessRequested using NSUserDefaults
- (BOOL)returnMediaAccessRequested;

#pragma mark show loading view
// show loading view (Loading... label with activity indicator) on a view
- (void)showLoadingViewOnView:(UIView *)view;

#pragma mark hide loading view
// hide loading view (Loading... label with activity indicator)
- (void)hideLoadingView;

#pragma mark show loading view and NetworkActivityIndicator
// show loading view (Loading... label with activity indicator) and NetworkActivityIndicator
- (void)showLoadingViewAndNetworkActivityIndicatorOnView:(UIView *)view;

#pragma mark hide loading view and NetworkActivityIndicator
// hide loading view (Loading... label with activity indicator) and NetworkActivityIndicator
- (void)hideLoadingViewAndNetworkActivityIndicator;

#pragma mark replace new line characters in a string
// replaces \n (new line characters) in string with "string to replace with"
- (NSString *)replaceNewLineCharacterSetIn:(NSString *)stringToModify with:(NSString *)stringToReplaceNewLineCharactersWith;

@end
