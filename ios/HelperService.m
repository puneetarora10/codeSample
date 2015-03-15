//
//  HelperService.m
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import "HelperService.h"
#import "QuartzCore/QuartzCore.h"
#import "HomeViewController.h"
#import "AssetsLibrary/AssetsLibrary.h"

// private declarations
@interface HelperService ()
// activityIndicator
@property (strong, nonatomic) UIActivityIndicatorView *activityIndicator;
// loading view
@property (strong, nonatomic) UIView *loadingView;
@end

@implementation HelperService

#define FULL_NAME_KEY @"fullName"
#define EMAIL_KEY @"email"
#define PROFILE_PICTURE_URL_KEY @"profilePictureURLString"
#define USER_SINCE_KEY @"userSince"
#define USER_ROLE_KEY @"userRole"
#define ATTACHMENTS_COUNT_KEY @"attachmentsCount"
#define GENERIC_ERROR_MESSAGE @"Sorry, Slow Internet Connection on your device!!"

// returns a default instance of HelperService to implement singleton
// in other words only one instance of HelperService object exists in the application
+ (HelperService *)defaultInstance {
    static HelperService *_defaultInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _defaultInstance = [[HelperService alloc] init];
    });
    
    return _defaultInstance;
}

// initializes activityIndicator
- (UIActivityIndicatorView *)activityIndicator {
    if(!_activityIndicator) {
        _activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        
    }
    return _activityIndicator;
}

// initializes loadingView
- (UIView *)loadingView {
    if (!_loadingView) {
        _loadingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 170, 170)];
        _loadingView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
        _loadingView.clipsToBounds = YES;
        _loadingView.layer.cornerRadius = 10.0;
        
    }
    return _loadingView;
}

// saves users' data after user has successfully loggedIn in to NSUserDefaults
// usersData = {"profilePictureURLString":"", "fullName": "", "email": "", "userSince":"", "userRole":"", "attachmentsCounts":""}
- (void)saveUsersData:(NSMutableDictionary *) usersData {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    // iterate usersData and save in defaults
    for (NSString *key in usersData) {
        [defaults setObject:[usersData objectForKey:key] forKey:key];
    }
}

// updates NSUserDefaults' profilePictureURLString
- (void)updateProfilePictureURLString:(NSString *)profilePictureURLString {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    // update profilePictureURLString
    [defaults setObject:profilePictureURLString forKey:PROFILE_PICTURE_URL_KEY];
}


// called when user logs out - clears NSUserDefaults for this app
- (void)clearNSUserDefaults {
    NSString *myAppDomain = [[NSBundle mainBundle] bundleIdentifier];
    [[NSUserDefaults standardUserDefaults] removePersistentDomainForName:myAppDomain];
}

// shows activity indicator on a view
- (void)showActivityIndicatorOnView:(UIView *)view withFrame:(CGRect)frame {
    // set activityIndicator's frame
    self.activityIndicator.frame = frame;
    [self.activityIndicator startAnimating];
    // add activityIndicator to the "view"
    [view addSubview:self.activityIndicator];
}

// hides activity indicator (already existing) on a view
- (void)hideActivityIndicator {
    [self.activityIndicator stopAnimating];
}

// makes "Squared" UIButton look like "Rounded Rectangle"
- (void)changeSquaredUIButtonToRoundedRect:(UIButton *)button {
    [button.layer setCornerRadius:7.0f];
    button.layer.masksToBounds = YES;
}

// returns YES if device is an IPAD
- (BOOL)checkIfCurrentDeviceIsAnIPAD {
    NSString *deviceType = [UIDevice currentDevice].model;
    if ([[deviceType uppercaseString] rangeOfString:@"IPAD"].location != NSNotFound) {
        return YES;
    }
    else {
        return NO;
    }
}

// return YES if user is logged in
- (BOOL)checkIfUserIsLoggedIn {
    BOOL userIsLoggedIn = NO;
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *fullName = nil;
    // check if "fullName" key exists
    if ((fullName = [defaults objectForKey:FULL_NAME_KEY])) {// userDetails are present
        userIsLoggedIn = YES;
    }
    
    return userIsLoggedIn;
}

// returns email of logged in user using NSUserDefaults
- (NSString *)returnLoggedInUsersEmail {
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    // return email
    return [defaults objectForKey:EMAIL_KEY];
}

// returns attachmentsCount of logged in user using NSUserDefaults
- (NSString *)returnLoggedInUsersAttachmentsCount {
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    // return attachmentsCount
    return [defaults objectForKey:ATTACHMENTS_COUNT_KEY];
}

// returns YES if logged in user is an "ARTIST" or in other terms has the userRole = "ARTIST"
- (BOOL)checkIfLoggedInUserIsAnArtist {
    BOOL isLoggedInUserAnArtist = NO;
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([[defaults objectForKey:USER_ROLE_KEY] isEqualToString:@"ARTIST"]) {// user's role is "ARTIST"
        isLoggedInUserAnArtist = YES;
    }
    
    return isLoggedInUserAnArtist;
}

// return YES if logged in user is an "ADMINISTRATOR" or in other terms has the userRole = "ADMINISTRATOR"
- (BOOL)checkIfLoggedInUserIsAnAdministrator {
    BOOL isLoggedInUserAnAdministrator = NO;
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([[defaults objectForKey:USER_ROLE_KEY] isEqualToString:@"ADMINISTRATOR"]) {// user's role is "ADMINISTRATOR"
        isLoggedInUserAnAdministrator = YES;
    }
    
    return isLoggedInUserAnAdministrator;
}

// returns YES if logged in user is a "TALENT" or in other terms has the userRole = "ARTIST" or "WRITER" OR "PRODUCER"
- (BOOL)checkIfLoggedInUserIsATalent {
    BOOL isLoggedInUserATalent = NO;
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userRole = [defaults objectForKey:USER_ROLE_KEY];
    if ([userRole isEqualToString:@"ARTIST"] || [userRole isEqualToString:@"WRITER"] || [userRole isEqualToString:@"PRODUCER"]) {// user's role is "ARTIST" or "WRITER" OR "PRODUCER"
        isLoggedInUserATalent = YES;
    }
    
    return isLoggedInUserATalent;
}

// returns YES if logged in user is an "AandR" or in other terms has the userRole = "AANDR"
- (BOOL)checkIfLoggedInUserIsAnAandR {
    BOOL isLoggedInUserAnAandR = NO;
    // get standardUserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userRole = [defaults objectForKey:USER_ROLE_KEY];
    if ([userRole isEqualToString:@"AANDR"]) {// user's role is "AANDR"
        isLoggedInUserAnAandR = YES;
    }
    
    return isLoggedInUserAnAandR;
}

// redirects to MeViewController
- (void)redirectToMeViewController:(UIViewController *)currentViewController {
    [currentViewController.tabBarController setSelectedIndex:2];
}

// redirects to HomeViewController and sets HomeViewController's searchBar's text
- (void)redirectToHomeViewController:(UIViewController *)currentViewController withSearchTerm:(NSString *)searchTerm {
    HomeViewController *homeViewController = [[[[currentViewController.tabBarController viewControllers] objectAtIndex:0] viewControllers] objectAtIndex:0];
    homeViewController.searchBar.text = searchTerm;
    [currentViewController.tabBarController setSelectedIndex:0];
}

// show an alertView with a title, a message and a cancel button with buttonTitle
- (void)showAlertViewWithTitle:(NSString *)title andMessage:(NSString *)message andACancelButtonWithTitle:(NSString *)buttonTitle {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:buttonTitle otherButtonTitles:nil, nil];
    [alertView show];
}

// show an alertView for giving GotSigned access to Media/ Photos
- (void)showAlertViewForGivingMediaAccess {
    [self showAlertViewWithTitle:@"" andMessage:@"Allow GotSigned access to your photos. \n Just go to Settings > Privacy > Photos and switch GotSigned to ON." andACancelButtonWithTitle:@"OK"];
}

// returns size of file in MB
- (long)returnSizeOfFileInMBWithMediaURL:(NSURL *)mediaURL {
    //Error Container
    NSError *attributesError;
    NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:[mediaURL path] error:&attributesError];
    NSNumber *fileSizeNumber = [fileAttributes objectForKey:NSFileSize];
    long fileSize = [fileSizeNumber longValue];
    // in KB
    long fileSizeInKB = fileSize / 1024;
    // in MB
    long fileSizeInMB = fileSizeInKB / 1024;
    
    return fileSizeInMB;
}

// returns YES if app has access to Media (PHOTOS)
- (BOOL)checkIfAppHasAccessToMedia {
    BOOL hasAccessToMedia = NO;
    if ([ALAssetsLibrary authorizationStatus] == ALAuthorizationStatusAuthorized) {
        hasAccessToMedia = YES;
    }
    
    return hasAccessToMedia;
}

// returns YES if the email address looks valid
- (BOOL)checkIfEmailIsValid:(NSString *)email {
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
    NSPredicate *emailPredicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    
    return [emailPredicate evaluateWithObject:email];
}

// returns server's URL string
- (NSMutableString *)returnServersURLString {    
    // webserver
    return [NSMutableString stringWithString: @"http://gotsigned.com/gotsigned"];
}

// appends generic errorMessage
- (void)appendGenericErrorMessageTo:(NSMutableDictionary *)appendGenericErrorMessageToIt {
    [appendGenericErrorMessageToIt setObject:GENERIC_ERROR_MESSAGE forKey:@"errorMessage"];
}

// appends (key/value) to data in the format used by browsers to submit forms
- (void)appendFormDataWithKey:(NSString *)key andValue:(NSString *)value andBoundary:(NSString *)boundary toData:(NSMutableData *)data {
    [data appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\" \r\n",key] dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:[value dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
    if (boundary) { // if boundary exists
        [data appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    }
}

// appends file to data in the format used by browsers to submit forms
- (void)appendFormDataWithKey:(NSString *)key andFileName:(NSString *)fileName andContentType:(NSString *)contentType andFileToBeUploadedData:(NSData *)fileToBeUploadedData andBoundary:(NSString *)boundary toData:(NSMutableData *)data {
    [data appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n", key, fileName] dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:[[NSString  stringWithFormat:@"Content-Type: %@\r\n\r\n",contentType] dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:fileToBeUploadedData];
    [data appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
    if (boundary) { // if boundary exists
        [data appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    }
}

// saves fileUploadInProgress in to NSUserDefaults
- (void)saveFileUploadInProgress:(BOOL)fileUploadInProgress {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:fileUploadInProgress forKey:@"fileUploadInProgress"];
}

// saves fileUploadFailed in to NSUserDefaults
- (void)saveFileUploadFailed:(BOOL)fileUploadFailed {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:fileUploadFailed forKey:@"fileUploadFailed"];
}

// returns fileUploadInProgress using NSUserDefaults
- (BOOL)returnFileUploadInProgess {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:@"fileUploadInProgress"];
}

// returns fileUploadFailed using NSUserDefaults
- (BOOL)returnFileUploadFailed {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:@"fileUploadFailed"];
}

// saves mediaAccessRequested in to NSUserDefaults
- (void)saveMediaAccessRequested {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if (![defaults boolForKey:@"mediaAccessRequested"]) {
        [defaults setBool:YES forKey:@"mediaAccessRequested"];
    }
}

// returns mediaAccessRequested using NSUserDefaults
- (BOOL)returnMediaAccessRequested {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:@"mediaAccessRequested"];
}

// show loading view (Loading... label with activity indicator) on a view
- (void)showLoadingViewOnView:(UIView *)view {
    // modify activityIndicatorViewStyle
    self.activityIndicator.frame = CGRectMake(75, 75, 20, 20);
    self.activityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
    // add activityIndicator as subView to loadingView
    [self.loadingView addSubview:self.activityIndicator];
    // create Loading... label
    UILabel *loadingLabel = [[UILabel alloc] initWithFrame:CGRectMake(25, 115, 130, 22)];
    // set properties
    loadingLabel.backgroundColor = [UIColor clearColor];
    loadingLabel.textColor = [UIColor whiteColor];
    loadingLabel.adjustsFontSizeToFitWidth = YES;
    loadingLabel.text = @"Loading...";
    loadingLabel.textAlignment = NSTextAlignmentCenter;
    // add loadingLabel as subView to loadingView
    [self.loadingView addSubview:loadingLabel];
    // set loadingView's center to view's center
    self.loadingView.center = view.center;
    // add loadingView as subView to "view"
    [view addSubview:self.loadingView];
    // start activityIndicator
    [self.activityIndicator startAnimating];
}

// hide loading view (Loading... label with activity indicator)
- (void)hideLoadingView {
    // hideActivityIndicator
    [self hideActivityIndicator];
    // remove loadingView from superView
    [self.loadingView removeFromSuperview];
}

// show loading view (Loading... label with activity indicator) and NetworkActivityIndicator
- (void)showLoadingViewAndNetworkActivityIndicatorOnView:(UIView *)view {
    // show loadingView
    [self showLoadingViewOnView:view];
    
    // show NetworkActivityIndicator
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
}

// hide loading view (Loading... label with activity indicator) and NetworkActivityIndicator
- (void)hideLoadingViewAndNetworkActivityIndicator {
    // hide loadingView
    [self hideLoadingView];
    
    // hide NetworkActivityIndicator
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

// replaces \n (new line characters) in string with "string to replace with"
- (NSString *)replaceNewLineCharacterSetIn:(NSString *)stringToModify with:(NSString *)stringToReplaceNewLineCharactersWith {
    return [[stringToModify componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:stringToReplaceNewLineCharactersWith];
}

@end
