//
//  HomeViewController.h
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Attachment.h"
#import "PendingOperations.h"
#import "AttachmentThumbnailDownloader.h"

@interface HomeViewController : UITableViewController <AttachmentThumbnailDownloaderDelegate, UISearchBarDelegate, UIAlertViewDelegate>

// unwind segues
- (IBAction)cancelFromContact:(UIStoryboard *)segue;

// searchBar
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;
// attachments to be displayed
@property (strong, nonatomic) NSMutableArray *attachments;
// pending operations
@property (strong, nonatomic) PendingOperations *pendingOperations;
// plays attachment with id = aid and path = path
// as well as updates numberOfViews of the attachment
- (void)playAttachmentWithId:(NSString *)aid andPath:(NSString *)path;

@end
