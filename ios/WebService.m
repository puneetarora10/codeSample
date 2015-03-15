//
//  Handles all interaction with the servers
//  WebService.m
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import "WebService.h"
#import "HelperService.h"

@interface WebService ()

// server's url string
@property (strong, nonatomic) NSMutableString *serverURLString;

@end


@implementation WebService

#pragma mark - Initialization Methods

// returns a default instance of WebService to implement singleton
// in other words only one instance of WebService object exists in the application
+ (WebService *)defaultInstance {
    static WebService *_defaultInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _defaultInstance = [[WebService alloc] init];
    });
    
    return _defaultInstance;
}

// initialize serverURLString
- (NSMutableString *)serverURLString {
    if(!_serverURLString) {
        _serverURLString = [[HelperService defaultInstance] returnServersURLString];
    }
    return _serverURLString;
}

#pragma mark - Web service calls

#pragma mark Create User
// creates user
// if any of the required textFields are empty then appends "errorMessage" to dataToBeReturned
// else makes a web service call and if user gets created sets userCreated to YES in dataToBeReturned
- (NSDictionary *)createUserWithFirstName:(NSString *)firstName andLastName:(NSString *)lastName andEmail:(NSString *)email andRoleOrDescription:(NSString *)roleOrDescription andReferredBy:(NSString *)referredBy andProfilePictureExtension:(NSString *)profilePictureExtension {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    if ([firstName length] > 0 && [lastName length] > 0 && [email length] > 0 && [roleOrDescription length] > 0) {
        // check if roleOrDescription is "APPRECIATOR" then change it to "user"
        NSString *upperCaseRoleOrDescription = [[roleOrDescription uppercaseString]stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([upperCaseRoleOrDescription isEqualToString:@"APPRECIATOR"]) {
            roleOrDescription = @"user";
        }
        else if ([upperCaseRoleOrDescription isEqualToString:@"A&R"]) {// replace A&R by AandR
            roleOrDescription = @"AandR";
        }
        // create URL
        NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/createUser"];
        NSURL *serverURL = [NSURL URLWithString:serverURLString];
        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
        // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
        [urlRequest setTimeoutInterval:15.0f];
        [urlRequest setHTTPMethod:@"POST"];
        
        // create params to be send in POST request
        NSString *params = @"firstName=";
        // append textFields' text to params
        params = [params stringByAppendingFormat:@"%@&lastName=%@&email=%@&userRole=%@&referredBy=%@",firstName, lastName, email, roleOrDescription, referredBy];
        // if profilePictureExtension exists append it to params
        if ([profilePictureExtension length] > 0) {
            params = [params stringByAppendingFormat:@"&profilePictureExtension=%@",profilePictureExtension];
        }
        [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
        
        // create synchronous request as the user should not be able to do anything else unless he logs in!!
        NSError *error = nil;
        NSHTTPURLResponse *response = nil;
        @try {
            NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
            if ([data length] > 0 && error == nil) {
                // set usersData to be used by MeViewController
                NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
                if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // some error while creating user
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
                }
                else { // user was successfully created
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"flashMessage"] forKey:@"flashMessage"];
                    [dataToBeReturned setObject:@"YES" forKey:@"userCreated"];
                }
            }
            else { // append generic errorMessage
                [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
            }
        }
        @catch (NSException *exception) { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
        @finally {
            // no need to do anything right now
        }
    }
    else { // append "errorMessage"
        [dataToBeReturned setObject:@"All Fields are mandatory. Please enter all the fields. Thanks!" forKey:@"errorMessage"];
    }
    
    return dataToBeReturned;
}

#pragma mark Change User's Password
// changes user's password
// if any of the required textFields are empty then appends "errorMessage" to dataToBeReturned
// else makes a web service call and if user gets created sets passwordChanged to YES in dataToBeReturned
- (NSDictionary *)changePasswordForUserWithEmail:(NSString *)email password:(NSString *)password andTemporaryPassword:(NSString *)temporaryPassword {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    if ([temporaryPassword length] > 0 && [password length] > 0 && [email length] > 0) {
        // create URL
        NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/changePassword"];
        NSURL *serverURL = [NSURL URLWithString:serverURLString];
        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
        // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
        [urlRequest setTimeoutInterval:15.0f];
        [urlRequest setHTTPMethod:@"POST"];
        
        // create params to be send in POST request
        NSString *params = @"temporaryPassword=";
        // append textFields' text to params
        params = [params stringByAppendingFormat:@"%@&newPassword=%@&username=%@",temporaryPassword,password,email];
        [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
        
        // create synchronous request as the user should not be able to do anything else unless he logs in!!
        NSError *error = nil;
        NSHTTPURLResponse *response = nil;
        @try {
            NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
            if ([data length] > 0 && error == nil) {
                // serialize dataReturnedFromServer
                NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
                if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // some error while changing user's password
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
                }
                else { // password was successfully changed
                    [dataToBeReturned setObject:@"YES" forKey:@"passwordChanged"];
                }
            }
            else { // append generic errorMessage
                [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
            }
        }
        @catch (NSException *exception) { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
        @finally {
            // no need to do anything right now
        }
    }
    else { // append "errorMessage"
        [dataToBeReturned setObject:@"All Fields are mandatory. Please enter all the fields. Thanks!" forKey:@"errorMessage"];
    }
    
    return dataToBeReturned;
}

#pragma mark Authenticate User
// make a web service call to check if user exists in the system
// if yes userDetails would be returned by the server
// else errorMessage
- (NSMutableDictionary *)authenticateUserWithEmail:(NSString *)email andPassword:(NSString *)password {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    if ([email length] > 0 && [password length] > 0) {// both email and password have been entered
        // make a post request to authenticate user
        // create URL
        NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/authenticateUser"];
        NSURL *serverURL = [NSURL URLWithString:serverURLString];
        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
        // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
        [urlRequest setTimeoutInterval:15.0f];
        [urlRequest setHTTPMethod:@"POST"];
        
        // create params to be send in POST request
        NSString *params = @"inputEmail=";
        // append email and password
        params = [params stringByAppendingFormat:@"%@&inputPassword=%@",email,password];
        [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
        
        // create synchronous request as the user should not be able to do anything else unless he logs in!!
        NSError *error = nil;
        NSHTTPURLResponse *response = nil;
        @try {
            NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
            if ([data length] > 0 && error == nil) {
                // serialize data returned from server
                NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
                // check if dataReturnedFromServer contains an errorMessage
                if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // user couldn't be authenticated
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
                }
                else { // user was successfully authenticated
                    [dataToBeReturned setObject:dataReturnedFromServer forKey:@"usersData"];
                    [dataToBeReturned setObject:@"YES" forKey:@"userAuthenticated"];
                }
            }
            else { // append generic errorMessage
                [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
            }
        }
        @catch (NSException *exception) { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
        @finally {
            // no need to do anything right now
        }
    }
    else { // append an errorMessage "Both Email and Password are required"
        [dataToBeReturned setObject:@"Both Email and Password are Required" forKey:@"errorMessage"];
    }
    
    return dataToBeReturned;
}

#pragma mark Get Authenticated User's properties
// web service call to get already authenticated user's properties
- (NSMutableDictionary *)returnPropertiesOfAuthenticatedUserWithEmail:(NSString *)email {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    // make a post request to get already authenticated user's properties
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/returnAuthenticatedUsersProperties"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
    [urlRequest setTimeoutInterval:15.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    // create params to be send in POST request
    NSString *params = @"username=";
    // append email and password
    params = [params stringByAppendingFormat:@"%@",email];
    [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    
    // create synchronous request as the user should not be able to do anything else unless he logs in!!
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            // check if dataReturnedFromServer contains an errorMessage
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // some error while retrieving user's properties
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
            }
            else { // user's properties have been retrieved
                [dataToBeReturned setObject:dataReturnedFromServer forKey:@"usersData"];
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

#pragma mark Update User's Profile Picture
// updates users' profile picture
// and then updates NSUserDefaults' profilePictureURLString
- (NSDictionary *)updateUsersProfilePictureWithUsername:(NSString *)username andProfilePictureData:(NSData *)profilePictureData andProfilePictureExtension:(NSString *)profilePictureExtension {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    // create request
    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] init];
    // no need to set timeout interval as this web service call is made on a different thread
    [urlRequest setHTTPMethod:@"POST"];
    
    // set Content-Type in HTTP header
    // some random number
    NSString *boundary = @"56738788";
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
    [urlRequest setValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    // post body
    NSMutableData *body = [NSMutableData data];
    // add image data
    if (profilePictureData) {
        [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
        // append profilePictureData
        [[HelperService defaultInstance] appendFormDataWithKey:@"fileProfilePicture" andFileName:@"profilePicture.jpg" andContentType:@"image/jpeg" andFileToBeUploadedData:profilePictureData andBoundary:nil toData:body];
    }
    
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    // setting the body of the post to the reqeust
    [urlRequest setHTTPBody:body];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingFormat:@"/mobileOrTablet/updateUsersProfilePicture?username=%@&profilePictureExtension=%@",username,profilePictureExtension];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    [urlRequest setURL:serverURL];
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // some errorMessage returned from the server
                [dataToBeReturned setObject:@"Sorry, Slow Internet Connection on your device!!" forKey:@"errorMessage"];
            }
            else {// profilePicture has been updated
                if ([dataReturnedFromServer objectForKey:@"profilePicturePath"]) {
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"profilePicturePath"] forKey:@"profilePicturePath"];
                }
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

#pragma mark Get Attachments' data
// make a web service call to get attachments' data
// if data is recieved from server then return that data
// else errorMessage
- (id)returnAttachmentsDataWithSearchTerm:(NSString *)searchTerm {
    id dataToBeReturned;
    // make a post request to authenticate user
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/returnAttachmentsDataAsJSON"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
    [urlRequest setTimeoutInterval:15.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    if (searchTerm) {// check if searchTerm exists
        // create params to be send in POST request
        NSString *params = @"searchTerm=";
        // append email and password
        params = [params stringByAppendingFormat:@"%@",searchTerm];
        [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    // create an asynchronous request
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        // no need for synchronous request as its called on a different queue
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            id dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            // check if dataReturnedFromServer is an instance of NSDictionary
            if ([dataReturnedFromServer isKindOfClass:[NSDictionary class]]) {
                // check if dataReturnedFromServer contains an errorMessage
                if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // attachment's data couldn't be downloaded
                    dataToBeReturned = [[NSMutableDictionary alloc] init];
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
                }
            }
            else if ([dataReturnedFromServer isKindOfClass:[NSArray class]]) {// dataReturnedFromServer is an instance of NSArray
                dataToBeReturned = [[NSMutableArray alloc] init];
                for (id element in dataReturnedFromServer) {
                    [dataToBeReturned addObject:[element mutableCopy]];
                }
            }
            else { // attachment's data couldn't be downloaded // append generic errorMessage
                dataToBeReturned = [[NSMutableDictionary alloc] init];
                [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
            }
        }
        else {// append generic errorMessage
            dataToBeReturned = [[NSMutableDictionary alloc] init];
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

#pragma mark Update Attachment's numberOfViews
// web service call to update numberOfViews on Attachment with id=aid
- (void)updateNumberOfViewsForAttachmentWithId:(NSString *)aid {
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/updateNumberOfViews"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // no need to set timeout interval as this web service call is made on a different thread
    [urlRequest setHTTPMethod:@"POST"];
    
    // create params to be send in POST request
    NSString *params = @"aid=";
    // append email and password
    params = [params stringByAppendingFormat:@"%@",aid];
    [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        // no need for synchronous request as its called on a different queue
        [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
    }
    @catch (NSException *exception) {
        // no need to do anything right now
    }
    @finally {
        // no need to do anything right now
    }
}

#pragma mark Upload Attachment
// web service call to upload an attachment (audio or video file)
// fileNameOnSystem (in other words name which would be used to create the file on the system)
// and fileNameOnSystem_Poster would be used for poster's system name (in other words name which would be used to create the file's poster on the system)
- (NSDictionary *)uploadAttachmentWithUserEmail:(NSString *)email andFileName:(NSString *)fileName andFileDescription:(NSString *)fileDescription andFilePoster:(NSData *)filePosterData andFilePosterURL:(NSString *)filePosterURL andFileToBeUploaded:(NSData *)fileToBeUploadedData andFileNameOnSystem:(NSString *)fileNameOnSystem andFilePosterExtension:(NSString *)filePosterExtension andFileToBeUploadedExtension:(NSString *)fileToBeUploadedExtension andFileToBeUploadedType:(NSString *)fileToBeUploadedType andFileHashTags:(NSString *)fileHashTags {
    // data to be returned
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];

    // create request
    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] init];
    // set HTTP method to be POST
    [urlRequest setHTTPMethod:@"POST"];
    
    // set Content-Type in HTTP header
    // some random number
    NSString *boundary = @"56738788";
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
    [urlRequest setValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    // post body
    NSMutableData *body = [NSMutableData data];
    [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    
    // append filePoster
    [[HelperService defaultInstance] appendFormDataWithKey:@"filePoster" andFileName:[NSString stringWithFormat:@"%@_Poster.%@", fileNameOnSystem, filePosterExtension] andContentType:[NSString stringWithFormat:@"image/%@", filePosterExtension] andFileToBeUploadedData:filePosterData andBoundary:boundary toData:body];
    
    // append filePosterURL
    [[HelperService defaultInstance] appendFormDataWithKey:@"filePosterURL" andValue:filePosterURL andBoundary:boundary toData:body];
    
    //append fileToBeUploaded or attachment to be uploaded on the server
    [[HelperService defaultInstance] appendFormDataWithKey:@"fileToBeUploaded" andFileName:[NSString stringWithFormat:@"%@.%@", fileNameOnSystem, fileToBeUploadedExtension] andContentType:[NSString stringWithFormat:@"%@/%@", fileToBeUploadedType, fileToBeUploadedExtension] andFileToBeUploadedData:fileToBeUploadedData andBoundary:boundary toData:body];
    
    // append fileName
    [[HelperService defaultInstance] appendFormDataWithKey:@"fileName" andValue:fileName andBoundary:boundary toData:body];
    
    // append fileDescription
    [[HelperService defaultInstance] appendFormDataWithKey:@"fileDescription" andValue:fileDescription andBoundary:boundary toData:body];
    
    // append fileHashTags or hashTags
    [[HelperService defaultInstance] appendFormDataWithKey:@"hashTags" andValue:fileHashTags andBoundary:boundary toData:body];
    
    // append username or email
    [[HelperService defaultInstance] appendFormDataWithKey:@"username" andValue:email andBoundary:boundary toData:body];
    
    
    // append attachmentType
    [[HelperService defaultInstance] appendFormDataWithKey:@"attachmentType" andValue:fileToBeUploadedType andBoundary:nil toData:body];
    
    
    // end boundary
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    
    // setting the body of the post to the reqeust
    [urlRequest setHTTPBody:body];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingFormat:@"/mobileOrTablet/uploadAttachment"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    [urlRequest setURL:serverURL];
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    
    @try {
        // create synchronous request as its being called on a different queue (automatically it becomes asynchronous)
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        // check if there is any error or file has been uploaded
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // errorMessage exists
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
            }
            else { // file has been uploaded
                [dataToBeReturned setObject:@"Thanks! We will send an email when your media file approved!" forKey:@"flashMessage"];
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"attachmentId"] forKey:@"attachmentId"];
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

#pragma mark Get Trending HashTags
// web service call to get trending hashTags
- (NSDictionary *)returnTrendingHashTags {
    // data to be returned
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/returnTrendingHashTags"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
    [urlRequest setTimeoutInterval:15.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    
    @try {
        // synchronous request because table expects data.. asyn doesnt seem to work
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            [dataToBeReturned setObject:dataReturnedFromServer forKey:@"hashTags"];
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

# pragma mark Append data to Attachment
// web service call to append data to attachment
- (NSDictionary *)appendDataToAttachmentWithId:(NSString *)attachmentId andFileNameOnSystem:(NSString *)fileNameOnSystem andFileData:(NSData *)fileData andFileToBeUploadedExtension:(NSString *)fileToBeUploadedExtension andFileToBeUploadedType:(NSString *)fileToBeUploadedType {
    // data to be returned
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    
    // create request
    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] init];
    // set HTTP method to be POST
    [urlRequest setHTTPMethod:@"POST"];
    
    
    // set Content-Type in HTTP header
    // some random number
    NSString *boundary = @"56738788";
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
    [urlRequest setValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    // post body
    NSMutableData *body = [NSMutableData data];
    [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    //append fileData or attachment's chunks of data to be uploaded to be file on the server
    [[HelperService defaultInstance] appendFormDataWithKey:@"dataToBeAppendedToFile" andFileName:[NSString stringWithFormat:@"%@.%@", fileNameOnSystem, fileToBeUploadedExtension] andContentType:[NSString stringWithFormat:@"%@/%@", fileToBeUploadedType, fileToBeUploadedExtension] andFileToBeUploadedData:fileData andBoundary:boundary toData:body];
    
    // append attachmentId
    [[HelperService defaultInstance] appendFormDataWithKey:@"attachmentId" andValue:attachmentId andBoundary:nil toData:body];
    
    // end boundary
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];    
    
    // setting the body of the post to the reqeust
    [urlRequest setHTTPBody:body];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingFormat:@"/mobileOrTablet/appendDataToAttachment"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    [urlRequest setURL:serverURL];
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    
    @try {
        // no need for synchronous request as its called on a different queue
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        // check if there is any error or file has been uploaded
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // errorMessage exists
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

#pragma mark Forgot Password?
// web service call when user has forgotten his password and clicks on "Forgot Password?"
- (NSDictionary *)forgotPasswordForEmail:(NSString *)email {
    // data to be returned
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/forgotPassword"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
    [urlRequest setTimeoutInterval:15.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    // create params to be send in POST request
    NSString *params = @"username=";
    // append email
    params = [params stringByAppendingFormat:@"%@",email];
    [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    
    // create synchronous request as the user should not be able to do anything else gets his new password or an alert view if any errors!!
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            // check if dataReturnedFromServer contains an errorMessage
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // user with username = email doesn't exist
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
            }
            else if([dataReturnedFromServer objectForKey:@"flashMessage"]) { // user with username = email exists
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"flashMessage"] forKey:@"flashMessage"];
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }

    return dataToBeReturned;
}

#pragma mark Send Message to Talent/ Create Conversation
// web service call to send message to talent or in other words create conversation
- (NSDictionary *)sendMessage:(NSString *)messageText toTalent:(NSString *)receiver fromUserWithEmail:(NSString *)sender {
    // data to be returned
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingString:@"/mobileOrTablet/createConversation"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:serverURL];
    // setTimeoutInterval 15.0f so that the request should be finished in 15 seconds (successfully or unsuccessfully)
    [urlRequest setTimeoutInterval:15.0f];
    [urlRequest setHTTPMethod:@"POST"];
    
    // create params to be send in POST request
    NSString *params = @"messageText=";
    // append messageText, receiver and sender
    params = [params stringByAppendingFormat:@"%@&receiver=%@&sender=%@", messageText, receiver, sender];
    [urlRequest setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    
    // create a synchronous request
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        
        // check if there is any error or file has been uploaded
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // errorMessage exists
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"errorMessage"] forKey:@"errorMessage"];
            }
            else if ([dataReturnedFromServer objectForKey:@"flashMessage"]) { // flashMessage exists
                [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"flashMessage"] forKey:@"flashMessage"];
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

# pragma mark Append data to Attachment's poster
// web service call to append data to attachment's poster
- (NSDictionary *)appendPosterDataToAttachmentWithId:(NSString *)attachmentId andFilePosterData:(NSData *)filePosterData andFilePosterExtension:(NSString *)filePosterExtension andFileNameOnSystem:(NSString *) fileNameOnSystem {
    NSMutableDictionary *dataToBeReturned = [[NSMutableDictionary alloc] init];
    // create request
    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] init];
    // set HTTP method to be POST
    [urlRequest setHTTPMethod:@"POST"];    
    
    // set Content-Type in HTTP header
    // some random number
    NSString *boundary = @"56738788";
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
    [urlRequest setValue:contentType forHTTPHeaderField: @"Content-Type"];
    
    // post body
    NSMutableData *body = [NSMutableData data];
    [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    // append chunk of filePoster to be uploaded
    [[HelperService defaultInstance] appendFormDataWithKey:@"dataToBeAppendedToThumbnail" andFileName:[NSString stringWithFormat:@"%@_Poster.%@", fileNameOnSystem, filePosterExtension] andContentType:[NSString stringWithFormat:@"image/%@", filePosterExtension] andFileToBeUploadedData:filePosterData andBoundary:boundary toData:body];
    
    // append attachmentId
    [[HelperService defaultInstance] appendFormDataWithKey:@"attachmentId" andValue:attachmentId andBoundary:boundary toData:body];
    
    // append filePosterExtension
    [[HelperService defaultInstance] appendFormDataWithKey:@"filePosterExtension" andValue:filePosterExtension andBoundary:nil toData:body];
    
    // end boundary
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    
    // setting the body of the post to the reqeust
    [urlRequest setHTTPBody:body];
    
    // create URL
    NSString *serverURLString = [self.serverURLString stringByAppendingFormat:@"/mobileOrTablet/appendDataToAttachmentsThumbnail"];
    NSURL *serverURL = [NSURL URLWithString:serverURLString];
    [urlRequest setURL:serverURL];
    NSError *error = nil;
    NSHTTPURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
        if ([data length] > 0 && error == nil) {
            // serialize data returned from server
            NSDictionary *dataReturnedFromServer = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
            if ([dataReturnedFromServer objectForKey:@"errorMessage"]) { // some errorMessage returned from the server
                [dataToBeReturned setObject:@"Sorry, Slow Internet Connection on your device!!" forKey:@"errorMessage"];
            }
            else {// profilePicture has been updated
                if ([dataReturnedFromServer objectForKey:@"profilePicturePath"]) {
                    [dataToBeReturned setObject:[dataReturnedFromServer objectForKey:@"profilePicturePath"] forKey:@"profilePicturePath"];
                }
            }
        }
        else { // append generic errorMessage
            [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
        }
    }
    @catch (NSException *exception) { // append generic errorMessage
        [[HelperService defaultInstance] appendGenericErrorMessageTo:dataToBeReturned];
    }
    @finally {
        // no need to do anything right now
    }
    
    return dataToBeReturned;
}

@end
