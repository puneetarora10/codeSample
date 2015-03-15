package gotsigned

import grails.converters.JSON

class AdminController {
    // include beans
    def userService
    def adminService
    def mailService
    def grailsApplication

    /**
     * todo right now showing all attachments with approved flag = false
     * todo down the road can make do it by ajax
     * todo can use pagination panel
     * admin's page for managing (approving/ disapproving/ deleting) attachments
     */
    def manageAttachmentsDashboard = {
        User user = session.user

        if (user) {
            // check if user is an admin
            Boolean isAdmin = userService.checkIfUserIsAdmin(user)
            if (isAdmin) { // user is an admin
                // get all attachments
                List<Attachment> attachments = Attachment.findAllByApproved(false)

                render(view: 'manageAttachments', model: [attachments: attachments])
            }
            else {
                redirect(controller: 'home', action: 'searchDashboard')
            }
        }
        else {
            redirect(controller: 'home', action: 'searchDashboard')
        }
    }

    /**
     * sets approved flag to true
     * sends email to talent who uploaded the attachment
     * sends email to followers
     * @param attachmentId
     */
    def approveAttachment = {
        // find attachment
        Attachment attachment = Attachment.findById(params?.attachmentId?.toLong())
        adminService.approveAttachment(attachment)
        User talent = attachment?.user
        // send emails
        sendEmailToTalentAndFollowers(talent, attachment)
        // todo remove this down the road
        // send emails to all users about new attachment
        // sendEmailToAllUsers(talent, attachment)

        Map results = [:]
        results.put("result", "success")

        render results as JSON
    }

    /**
     * sends email to all users when an attachment is approved
     * @param attachment
     */
    private void sendEmailToAllUsers(User talent, Attachment attachment) {
        String attachmentsUrl = "${grailsApplication.config.getProperty("websiteUrlBase")}home/playAttachmentDashboard?aid=${attachment.id}"

        // find all users in system
        List<User> users = User?.findAll()

        if (users.size() > 0) {
            // send mail
            mailService.sendMail {
                to "noreply@gotsigned.com"
                bcc users?.email?.toArray()
                subject "Hey you! Check out a new upload at www.gotsigned.com !"
                html g.render(template: "/mailNotifications/attachmentAvailableAllUsers", model: [user: talent, attachmentsUrl: attachmentsUrl])
            }
        }
    }

    /**
     * todo to be moved to email controller and be called using ajax
     * @param talent
     * @param attachment
     */
    private void sendEmailToTalentAndFollowers(User talent, Attachment attachment) {
        String attachmentsUrl = "${grailsApplication.config.getProperty("websiteUrlBase")}home/playAttachmentDashboard?aid=${attachment.id}"
        // send email to talent
        sendEmailToTalent(talent, attachmentsUrl)
        // send email to followers
        sendEmailToFollowers(talent, attachmentsUrl)
    }

    /**
     * sends approval notification to artist whose video has been approved
     * @param talent
     * @param attachmentsUrl
     */
    private void sendEmailToTalent(User talent, String attachmentsUrl) {
        // send mail
        mailService.sendMail {
            to "${talent.email}"
            subject "Congratulations!! Your Media file has been approved"
            html g.render(template: "/mailNotifications/attachmentApproval", model: [user: talent, attachmentsUrl: attachmentsUrl])
        }
    }

    /**
     * sends media file available notification to all followees to
     * @param talent
     * @param attachmentsUrl
     */
    private void sendEmailToFollowers(User talent, String attachmentsUrl) {
        // find followers
        List<User> followers = Follow.findAllByFollowee(talent)?.follower

        if (followers.size() > 0) {
            // send mail
            mailService.sendMail {
                to followers?.email?.toArray()
                subject "New Media File uploaded by artist"
                html g.render(template: "/mailNotifications/attachmentAvailable", model: [user: talent, attachmentsUrl: attachmentsUrl])
            }
        }
    }

    /**
     * uploads media file to server without creating any attachment object
     * So, nothing gets saved in the database
     */
    def uploadMediaDashboard = {
        User user = session.user

        if (user) {
            // check if user is an admin
            Boolean isAdmin = userService.checkIfUserIsAdmin(user)
            if (isAdmin) { // user is an admin
                // show upload button
                render(view: 'uploadMediaAttachment')
            }
            else {
                redirect(controller: 'home', action: 'searchDashboard')
            }
        }
        else {
            redirect(controller: 'home', action: 'searchDashboard')
        }
    }

    /**
     * uploads image file to server without creating any attachment object
     * So, nothing gets saved in the database
     */
    def uploadImageDashboard = {
        User user = session.user

        if (user) {
            // check if user is an admin
            Boolean isAdmin = userService.checkIfUserIsAdmin(user)
            if (isAdmin) { // user is an admin
                // show upload button
                render(view: 'uploadImageAttachment')
            }
            else {
                redirect(controller: 'home', action: 'searchDashboard')
            }
        }
        else {
            redirect(controller: 'home', action: 'searchDashboard')
        }
    }

    /**
     * called when upload button in uploadMediaAttachment is clicked
     * uploads media file to server without creating any attachment object
     */
    def uploadMediaAttachment = {
        // get hard disk number and path for image and media files
        String hardDiskNumber = grailsApplication.config.getProperty("hardDiskNumber")
        String dirPathImage = getServletContext().getRealPath("images/user/${hardDiskNumber}")
        String dirPathMedia = getServletContext().getRealPath("media/user/${hardDiskNumber}")

        adminService.uploadAttachment(request, params, dirPathImage, dirPathMedia, false)
    }

    /**
     * called when upload button in uploadImageAttachment is clicked
     * uploads image file to server without creating any attachment object
     */
    def uploadImageAttachment = {
        // get hard disk number and path for image and media files
        String hardDiskNumber = grailsApplication.config.getProperty("hardDiskNumber")
        String dirPathImage = getServletContext().getRealPath("images/user/${hardDiskNumber}")
        String dirPathMedia = getServletContext().getRealPath("media/user/${hardDiskNumber}")

        adminService.uploadAttachment(request, params, dirPathImage, dirPathMedia, true)
    }   
}