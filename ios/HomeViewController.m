//
//  HomeViewController.m
//  GotSigned
//
//  Created by Puneet Arora on 6/6/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

#import "HomeViewController.h"
#import "WebService.h"
#import "HelperService.h"
#import "AttachmentCell.h"
// for playing attachment
#import "MediaPlayer/MediaPlayer.h"
// for sharing options
#import "Social/Social.h"
#import "ContactViewController.h"

@interface HomeViewController ()

// queue to be used to download stuff for HomeViewController
@property (strong, nonatomic) NSOperationQueue *queue;

// player
@property (strong, nonatomic) MPMoviePlayerController *player;
// viewDidLoadCalled = YES if viewDidLoad is called
// to be used to reloadTable in viewDidAppear if table didn't load in viewDidLoad (say for example because the server was down and attachment's data wasn't recieved) as viewDidLoad is called only once when the view is loaded in to memory
@property (assign, nonatomic) BOOL viewDidLoadCalled;
// YES if attachments were downloaded
@property (assign, nonatomic) BOOL attachmentDataDownloaded;
// to check if alertViews are to be shown after playAttachment
// alertViews should not be shown if an attachment is run using playAttachmentWithId because its possible
// that this attachment is not shown on mobile app as only 100 attachments are shown
@property (assign, nonatomic) BOOL showAlertViewsAfterPlayAttachment;

@end

@implementation HomeViewController

#define SHOW_CONTACT_VIEW_CONTROLLER_SEGUE_IDENTIFIER @"ShowContactViewController"
#define RETURN_FROM_CONTACT_VIEW_CONTROLLER_CANCEL_SEGUE_IDENTIFIER @"ReturnFromContactCancel"

#pragma mark - Initialization Methods

// initialize pendingOperations
- (PendingOperations *)pendingOperations {
    if (!_pendingOperations) {
        _pendingOperations = [[PendingOperations alloc] init];
    }
    return _pendingOperations;
}

// initialize attachments
- (NSMutableArray *)attachments {
    // if attachments doesn't exist (eg. called for the first time)
    if (!_attachments) {
        // show loading view (Loading... label with activity indicator) and NetworkActivityIndicator
        [[HelperService defaultInstance] showLoadingViewAndNetworkActivityIndicatorOnView:self.view];
        // download attachments from the server
        [self.queue addOperationWithBlock:^{
            id dataReturnedFromWebServiceCall = [[WebService defaultInstance] returnAttachmentsDataWithSearchTerm:self.searchBar.text];
            if ([dataReturnedFromWebServiceCall isKindOfClass:[NSArray class]]) { // attachments data is recieved
                _attachments = [[NSMutableArray alloc] init];
                // set attachmentDataDownloaded
                self.attachmentDataDownloaded = YES;
                for (NSDictionary *attachmentData in dataReturnedFromWebServiceCall) {
                    Attachment *attachment = [[Attachment alloc] init];
                    attachment.aid = [attachmentData objectForKey:@"id"];
                    attachment.name = [attachmentData objectForKey:@"name"];
                    attachment.numberOfViews = [attachmentData objectForKey:@"numberOfViews"];
                    attachment.type = [attachmentData objectForKey:@"attachmentType"];
                    attachment.path = [attachmentData objectForKey:@"path"];
                    attachment.thumbnailURL = [NSURL URLWithString:[attachmentData objectForKey:@"thumbnail"]];
                    attachment.webURL = [attachmentData objectForKey:@"webURL"];
                    attachment.uploadedByEmail = [attachmentData objectForKey:@"uploadedByEmail"];
                    attachment.uploadedByName = [attachmentData objectForKey:@"uploadedByName"];
                    [_attachments addObject:attachment];
                    attachment = nil;
                }
                // get hold of Main Queue to reload tableView
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    // hide loading view (Loading... label with activity indicator) and NetworkActivityIndicator
                    [[HelperService defaultInstance] hideLoadingViewAndNetworkActivityIndicator];
                    // reload tableView
                    [self.tableView reloadData];
                }];
            }
            else { // some errorMessage show "UIAlertView"
                // set attachmentDataDownloaded
                self.attachmentDataDownloaded = NO;
                // get hold of Main Queue to show UIAlertView
                [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                    // hide loading view (Loading... label with activity indicator) and NetworkActivityIndicator
                    [[HelperService defaultInstance] hideLoadingViewAndNetworkActivityIndicator];
                    // show alertView
                    [[HelperService defaultInstance] showAlertViewWithTitle:@"Error" andMessage:[dataReturnedFromWebServiceCall objectForKey:@"errorMessage"] andACancelButtonWithTitle:@"OK"];
                }];
            }
        }];        
    }
    return _attachments;
}

// initializing queue
- (NSOperationQueue *)queue {
    if (!_queue) {
        _queue = [[NSOperationQueue alloc] init];
        _queue.name = @"HomeViewController Queue";
    }
    return _queue;
}

// initializes attachments using searchString
// shows alert box if "No Records found"
- (void)initAttachmentsUsingSearchString:(NSString *)searchString {
    // show loading view (Loading... label with activity indicator) and NetworkActivityIndicator
    [[HelperService defaultInstance] showLoadingViewAndNetworkActivityIndicatorOnView:self.view];
    // download attachments from the server for searchString
    self.attachments = [[NSMutableArray alloc] init];
    // make web service call
    id dataReturnedFromWebServiceCall = [[WebService defaultInstance] returnAttachmentsDataWithSearchTerm:searchString];
    if ([dataReturnedFromWebServiceCall isKindOfClass:[NSArray class]]) { // attachments data is recieved
        for (NSDictionary *attachmentData in dataReturnedFromWebServiceCall) {
            Attachment *attachment = [[Attachment alloc] init];
            attachment.aid = [attachmentData objectForKey:@"id"];
            attachment.name = [attachmentData objectForKey:@"name"];
            attachment.numberOfViews = [attachmentData objectForKey:@"numberOfViews"];
            attachment.type = [attachmentData objectForKey:@"attachmentType"];
            attachment.path = [attachmentData objectForKey:@"path"];
            attachment.thumbnailURL = [NSURL URLWithString:[attachmentData objectForKey:@"thumbnail"]];
            attachment.webURL = [attachmentData objectForKey:@"webURL"];
            attachment.uploadedByEmail = [attachmentData objectForKey:@"uploadedByEmail"];
            attachment.uploadedByName = [attachmentData objectForKey:@"uploadedByName"];
            [self.attachments addObject:attachment];
            attachment = nil;
        }
        // reload tableView
        [self.tableView reloadData];
    }
    else { // some errorMessage show "UIAlertView"
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:[dataReturnedFromWebServiceCall objectForKey:@"errorMessage"] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alertView show];
    }
    // hide loading view (Loading... label with activity indicator) and NetworkActivityIndicator
    [[HelperService defaultInstance] hideLoadingViewAndNetworkActivityIndicator];
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    // set rowHeight
    self.tableView.rowHeight = 80.0;
    // set searchBar's delegate
    self.searchBar.delegate = self;
    [super viewDidLoad];
    
    // refreshControl - for refreshing the tableView
    UIRefreshControl *refreshTableViewControl = [[UIRefreshControl alloc] init];
    refreshTableViewControl.attributedTitle = [[NSAttributedString alloc] initWithString:@"Pull to Refresh"];
    [refreshTableViewControl addTarget:self action:@selector(refreshTableView:) forControlEvents:UIControlEventValueChanged];
    self.refreshControl = refreshTableViewControl;
    // set viewDidLoadCalled to YES
    self.viewDidLoadCalled = YES;
    // register HomeViewController as an observer for the notification PlayAttachmentFromURLScheme
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playAttachmentFromURLScheme:) name:@"PlayAttachmentFromURLScheme" object:nil];

}

- (void)viewWillAppear:(BOOL)animated {
    if (![self.searchBar.text isEqualToString:@""]) {// if searchBar's text exist
        [self initAttachmentsUsingSearchString:self.searchBar.text];
        [self.tableView reloadData];
    }
    else if(!self.viewDidLoadCalled && !self.attachmentDataDownloaded) {// viewDidLoad was not called so try reloadingData && attachmentDataDownloaded = NO
        [self.tableView reloadData];
    }
}

- (void) viewDidDisappear:(BOOL)animated {
    if (self.viewDidLoadCalled) {
        // set viewDidLoadCalled to NO
        self.viewDidLoadCalled = NO;
    }
}

- (void) dealloc {
    // unregister HomeViewController from any notifications
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

// refreshes the table view
// called when user pulls UITableView to refresh its data
- (void)refreshTableView:(UIRefreshControl *)refreshTableViewControl {
    // change refreshTableViewControl's title to "Refreshing..."
    refreshTableViewControl.attributedTitle = [[NSAttributedString alloc] initWithString:@"Refreshing..."];
    // reload tableView's data
    [self.tableView reloadData];
    // change refreshTableViewControl's title to "Last updated on.."
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MMM d, h:mm a"];
    NSString *lastUpdated = [NSString stringWithFormat:@"Last updated on %@",[dateFormatter stringFromDate:[NSDate date]]];
    refreshTableViewControl.attributedTitle = [[NSAttributedString alloc] initWithString:lastUpdated];
    // end refresh
    [refreshTableViewControl endRefreshing];
}

- (void)didReceiveMemoryWarning
{
    // cancel all operations
    [self cancelAllOperations];
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return self.attachments.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"AttachmentCell";
    AttachmentCell *attachmentCell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (!attachmentCell) {// create attachmentCell
        attachmentCell = [[AttachmentCell alloc] init];
    }
    // get attachment
    Attachment *attachment = [self.attachments objectAtIndex:indexPath.row];
    // Configure the cell
    attachmentCell.nameLabel.text = attachment.name;
    attachmentCell.numberOfViewsLabel.text = [NSString stringWithFormat:@"Views: %@",attachment.numberOfViews];
    // check if thumbnail is present
    if (!attachment.hasThumbnail || attachment.failedToDownloadThumbnail) {// if thumbnail is not present or thumbnail failed to download then download thumbnail
        if (!tableView.dragging && !tableView.decelerating) {
            [self startDownloadingThumbnailForAttachment:attachment atIndexPath:indexPath];
        }
    }
    else {// thumbnail is present
        attachmentCell.thumbnailImageView.image = attachment.thumbnail;
    }
    
    return attachmentCell;
}

// starts the thumbnail download operation for attachment at indexPath (UITableViewCell)
- (void)startDownloadingThumbnailForAttachment:(Attachment *)attachment atIndexPath:(NSIndexPath *)indexPath {
    // check if exists in pendingOperations
    if (![self.pendingOperations.downloadsInProgress.allKeys containsObject:indexPath]) {
        // start downloading thumbnail
        AttachmentThumbnailDownloader *attachmentThumbnailDownloader = [[AttachmentThumbnailDownloader alloc] initWithAttachment:attachment atIndexPath:indexPath delegate:self];
        // add to downloadsInProgress
        [self.pendingOperations.downloadsInProgress setObject:attachmentThumbnailDownloader forKey:indexPath];
        // add operation to queue
        [self.pendingOperations.downloadQueue addOperation:attachmentThumbnailDownloader];
    }
}

#pragma mark - AttachmentThumbnailDownloaderDelegate

- (void) attachmentThumbnailDownloaderDidFinish:(AttachmentThumbnailDownloader *) attachmentThumbnailDownloader {
    // get indexPath
    NSIndexPath *indexPath = attachmentThumbnailDownloader.indexPathInTableView;
    // check if indexPath exists in tableView's indexPathsForVisibleRows
    if ([self.tableView.indexPathsForVisibleRows containsObject:indexPath]) {
        // reload tableView at indexPath
        [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
    // remove indexPath from pendingOperations' downloadsInProgress
    [self.pendingOperations.downloadsInProgress removeObjectForKey:indexPath];
}


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    // find attachment for row selected
    Attachment *attachment = [self.attachments objectAtIndex:indexPath.row];
    // update numberOfViews on attachment
    [self updateNumberOfViewsForAttachmentWithId:attachment.aid];
    // play attachment
    [self playAttachment:attachment];
}

#pragma mark - MPMoviePlayer

// uses MPMoviePlayerController to play movie with URL = movieURL
- (void)playMovieWithURL:(NSURL *)movieURL {
    // initialize player
    self.player = [[MPMoviePlayerController alloc] initWithContentURL:movieURL];
    // add notifications
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(attachmentPlaybackDidFinish:) name:MPMoviePlayerPlaybackDidFinishNotification object:self.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(attachmentPlaybackDidFinish:) name:MPMoviePlayerDidExitFullscreenNotification object:self.player];
    
    self.player.controlStyle = MPMovieControlStyleDefault;
    self.player.shouldAutoplay = YES;    
    // add player's view
    [self.view.superview addSubview:self.player.view];
    [self.player prepareToPlay];
    [self.player setFullscreen:YES animated:YES];
}

// when MPMoviePlayerPlaybackDidFinishNotification/ MPMoviePlayerDidExitFullscreenNotification is recieved
- (void)attachmentPlaybackDidFinish:(NSNotification *)notification {
    // get player object
    MPMoviePlayerController *player = [notification object];
    // remove notifications to avoid crashes
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerPlaybackDidFinishNotification object:player];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerDidExitFullscreenNotification object:player];
    
    if ([player respondsToSelector:@selector(setFullscreen:animated:)]) {
        // remove player from superView
        [player.view removeFromSuperview];
        if (self.showAlertViewsAfterPlayAttachment) {
            // show share alertView if user is not AandR else perform segue with SHOW_CONTACT_VIEW_CONTROLLER_SEGUE_IDENTIFIER
            if ([[HelperService defaultInstance] checkIfLoggedInUserIsAnAandR]) {// loggedInUser is an "AandR"
                // show alertView to ask user if he/she wants to contact attachment's talent
                UIAlertView *wannaContactAttachmentsTalent = [[UIAlertView alloc] initWithTitle:@"Contact" message:@"Do you want to contact this artist?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
                [wannaContactAttachmentsTalent show];
            }
            else {
                // show alertView to ask user if he/she wants to share the attachment
                UIAlertView *wannaShareAlertView = [[UIAlertView alloc] initWithTitle:@"Share" message:@"Do you want to share this audio/ video?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
                [wannaShareAlertView show];
            }
        }
    }
}

# pragma mark - UIAlertViewDelegate

- (void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    // check if alertView is wannaShareAlertView
    if ([alertView.title isEqualToString:@"Share"]) {
        NSString *buttonTitle = [alertView buttonTitleAtIndex:buttonIndex];
        if ([buttonTitle isEqualToString:@"Yes"]) { // button's title is "Yes"
            [self showSharingOptions];
        }
    }
    else if ([alertView.title isEqualToString:@"Contact"]) { // check if alertView is wannaContactAttachmentsTalent
        NSString *buttonTitle = [alertView buttonTitleAtIndex:buttonIndex];
        if ([buttonTitle isEqualToString:@"Yes"]) { // button's title is "Yes"
            // show ContactViewController
            [self performSegueWithIdentifier:SHOW_CONTACT_VIEW_CONTROLLER_SEGUE_IDENTIFIER sender:self];
        }
    }
}

// shows sharing options
- (void)showSharingOptions {
    Attachment *selectedAttachment = [self.attachments objectAtIndex:[self.tableView indexPathForSelectedRow].row];
    NSArray *dataToShare = @[@"Amazing Talent!!",selectedAttachment.webURL];
    UIActivityViewController *activityViewController = [[UIActivityViewController alloc] initWithActivityItems:dataToShare applicationActivities:nil];
    [self presentViewController:activityViewController animated:YES completion:nil];
}

#pragma mark - UIScrollView delegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    // suspend all operations
    [self suspendAllOperations];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if (!decelerate) {
        [self loadThumbnailsForOnscreenCells];
        [self resumeAllOperations];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    [self loadThumbnailsForOnscreenCells];
    [self resumeAllOperations];
}

#pragma mark - Cancelling, suspending, resuming operations in pendingOperations' downloadQueue

- (void)suspendAllOperations {
    [self.pendingOperations.downloadQueue setSuspended:YES];
}

- (void)resumeAllOperations {
    [self.pendingOperations.downloadQueue setSuspended:NO];
}

- (void)cancelAllOperations {
    [self.pendingOperations.downloadQueue cancelAllOperations];
}

// loads thumbnails for only on screen cells
- (void)loadThumbnailsForOnscreenCells {
    // find visibleRows
    NSSet *visibleRows = [NSSet setWithArray:[self.tableView indexPathsForVisibleRows]];
    
    // find pendingOperations
    NSMutableSet *pendingOperations = [NSMutableSet setWithArray:[self.pendingOperations.downloadsInProgress allKeys]];
    // find operations to be cancelled and operations to be started
    NSMutableSet *toBeCancelled = [pendingOperations mutableCopy];
    NSMutableSet *toBeStarted = [visibleRows mutableCopy];
    
    [toBeStarted minusSet:pendingOperations];
    [toBeCancelled minusSet:visibleRows];
    
    // loop through toBeCancelled and cancel the operations
    for (NSIndexPath *anIndexPath in toBeCancelled) {
        AttachmentThumbnailDownloader *attachmentThumbnailDownloader = [self.pendingOperations.downloadsInProgress objectForKey:anIndexPath];
        [attachmentThumbnailDownloader cancel];
        [self.pendingOperations.downloadsInProgress removeObjectForKey:anIndexPath];
    }
    toBeCancelled = nil;
    
    // loop through toBeStarted and start the operations
    for (NSIndexPath *anIndexPath in toBeStarted) {
        Attachment *attachment = [self.attachments objectAtIndex:anIndexPath.row];
        [self startDownloadingThumbnailForAttachment:attachment atIndexPath:anIndexPath];
    }
    toBeStarted = nil;
}


#pragma mark - UISearchBarDelegate
// called when keyboard search button pressed
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    // remove cancel button
    searchBar.showsCancelButton = NO;
    // resignFirstResponder
    [searchBar resignFirstResponder];
    // initialize attachments
    [self initAttachmentsUsingSearchString:searchBar.text];
    // reload tableView
    [self.tableView reloadData];
}

// called when cancel button is clicked
- (void)searchBarCancelButtonClicked:(UISearchBar *) searchBar {
    // remove cancel button
    searchBar.showsCancelButton = NO;
    // clear searchBar's text and resignFirstResponder
    searchBar.text = @"";
    [searchBar resignFirstResponder];
    // initialize attachments
    [self initAttachmentsUsingSearchString:nil];
    // reload tableView
    [self.tableView reloadData];
}

// called when text starts editing
- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar {
    // show cancel button
    searchBar.showsCancelButton = YES;
}

#pragma mark - WebService Calls

- (void)updateNumberOfViewsForAttachmentWithId:(NSString *)aid {
    // updateNumberOfViewsForAttachmentWithId = aid
    [self.queue addOperationWithBlock:^{
        [[WebService defaultInstance] updateNumberOfViewsForAttachmentWithId:aid];
    }];
}

#pragma mark - Navigation/ Segue

#pragma mark Unwind segue
// called when "Cancel" button in ContactViewController is pressed/ tapped
- (IBAction)cancelFromContact:(UIStoryboardSegue *)segue {
    if ([[segue identifier] isEqualToString:RETURN_FROM_CONTACT_VIEW_CONTROLLER_CANCEL_SEGUE_IDENTIFIER]) {
        // already taken care of by ios7
        //[self dismissViewControllerAnimated:YES completion:NULL];
    }
}

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:SHOW_CONTACT_VIEW_CONTROLLER_SEGUE_IDENTIFIER]) {
        // find destinationViewController
        UINavigationController *navController = [segue destinationViewController];
        ContactViewController *contactViewController = (ContactViewController *) navController.visibleViewController;
        // find indexPathForSelectedRow
        NSIndexPath *indexPathForSelectedRow = [self.tableView indexPathForSelectedRow];
        // find attachment for row selected
        Attachment *attachment = [self.attachments objectAtIndex:indexPathForSelectedRow.row];
        // set contactViewController's uploadedByEmail and uploadedByName
        contactViewController.uploadedByEmail = attachment.uploadedByEmail;
        contactViewController.uploadedByName = attachment.uploadedByName;
    }
}

#pragma mark - Other Methods

// plays attachment
- (void)playAttachment:(Attachment *)attachment {
    // create url using attachment's path
    NSURL *attachmentURL = [NSURL URLWithString:attachment.path];
    // show alert views after attachment is played
    self.showAlertViewsAfterPlayAttachment = YES;
    [self playMovieWithURL:attachmentURL];
}

// play attachment with id = aid and path = path
// as well as updates numberOfViews of the attachment
- (void)playAttachmentWithId:(NSString *)aid andPath:(NSString *)path {
    // update numberOfViews
    [self updateNumberOfViewsForAttachmentWithId:aid];
    // create url using path
    NSURL *attachmentURL = [NSURL URLWithString:path];
    // show alert views after attachment is played
    self.showAlertViewsAfterPlayAttachment = NO;
    [self playMovieWithURL:attachmentURL];
}

// play attachment using notification PlayAttachmentFromURLScheme
- (void)playAttachmentFromURLScheme:(NSNotification *)notification {
    if (notification.userInfo[@"attachmentId"] && notification.userInfo[@"attachmentPath"]) {
        [self playAttachmentWithId:notification.userInfo[@"attachmentId"] andPath:notification.userInfo[@"attachmentPath"]];
    }
}

@end
