/** 
 * Copyright 2011 John M. Schanck
 * 
 * Scan2Zotero is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Scan2Zotero is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Scan2Zotero.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ale.scan2zotero.data;

public final class ItemField {
    // These are for translating between other APIs, such as Google Books
    // and Zotero's. The text for displaying each of these on screen is stored
    // in a localized form in our database.
    public static final String creators = "creators";
    public static final String itemType = "itemType";

    public final class Creator {
        public static final String firstName = "firstName";
        public static final String lastName = "lastName";
        public static final String name = "name";
    }

    public static final String numPages = "numPages";
    public static final String numberOfVolumes = "numberOfVolumes";
    public static final String abstractNote = "abstractNote";
    public static final String accessDate = "accessDate";
    public static final String applicationNumber = "applicationNumber";
    public static final String archive = "archive";
    public static final String artworkSize = "artworkSize";
    public static final String assignee = "assignee";
    public static final String billNumber = "billNumber";
    public static final String blogTitle = "blogTitle";
    public static final String bookTitle = "bookTitle";
    public static final String callNumber = "callNumber";
    public static final String caseName = "caseName";
    public static final String code = "code";
    public static final String codeNumber = "codeNumber";
    public static final String codePages = "codePages";
    public static final String codeVolume = "codeVolume";
    public static final String committee = "committee";
    public static final String company = "company";
    public static final String conferenceName = "conferenceName";
    public static final String country = "country";
    public static final String court = "court";
    public static final String DOI = "DOI";
    public static final String date = "date";
    public static final String dateDecided = "dateDecided";
    public static final String dateEnacted = "dateEnacted";
    public static final String dictionaryTitle = "dictionaryTitle";
    public static final String distributor = "distributor";
    public static final String docketNumber = "docketNumber";
    public static final String documentNumber = "documentNumber";
    public static final String edition = "edition";
    public static final String encyclopediaTitle = "encyclopediaTitle";
    public static final String episodeNumber = "episodeNumber";
    public static final String extra = "extra";
    public static final String audioFileType = "audioFileType";
    public static final String filingDate = "filingDate";
    public static final String firstPage = "firstPage";
    public static final String audioRecordingFormat = "audioRecordingFormat";
    public static final String videoRecordingFormat = "videoRecordingFormat";
    public static final String forumTitle = "forumTitle";
    public static final String genre = "genre";
    public static final String history = "history";
    public static final String ISBN = "ISBN";
    public static final String ISSN = "ISSN";
    public static final String institution = "institution";
    public static final String issue = "issue";
    public static final String issueDate = "issueDate";
    public static final String issuingAuthority = "issuingAuthority";
    public static final String journalAbbreviation = "journalAbbreviation";
    public static final String label = "label";
    public static final String language = "language";
    public static final String programmingLanguage = "programmingLanguage";
    public static final String legalStatus = "legalStatus";
    public static final String legislativeBody = "legislativeBody";
    public static final String libraryCatalog = "libraryCatalog";
    public static final String archiveLocation = "archiveLocation";
    public static final String interviewMedium = "interviewMedium";
    public static final String artworkMedium = "artworkMedium";
    public static final String meetingName = "meetingName";
    public static final String nameOfAct = "nameOfAct";
    public static final String network = "network";
    public static final String pages = "pages";
    public static final String patentNumber = "patentNumber";
    public static final String place = "place";
    public static final String postType = "postType";
    public static final String priorityNumbers = "priorityNumbers";
    public static final String proceedingsTitle = "proceedingsTitle";
    public static final String programTitle = "programTitle";
    public static final String publicLawNumber = "publicLawNumber";
    public static final String publicationTitle = "publicationTitle";
    public static final String publisher = "publisher";
    public static final String references = "references";
    public static final String reportNumber = "reportNumber";
    public static final String reportType = "reportType";
    public static final String reporter = "reporter";
    public static final String reporterVolume = "reporterVolume";
    public static final String rights = "rights";
    public static final String runningTime = "runningTime";
    public static final String scale = "scale";
    public static final String section = "section";
    public static final String series = "series";
    public static final String seriesNumber = "seriesNumber";
    public static final String seriesText = "seriesText";
    public static final String seriesTitle = "seriesTitle";
    public static final String session = "session";
    public static final String shortTitle = "shortTitle";
    public static final String studio = "studio";
    public static final String subject = "subject";
    public static final String system = "system";
    public static final String title = "title";
    public static final String thesisType = "thesisType";
    public static final String mapType = "mapType";
    public static final String manuscriptType = "manuscriptType";
    public static final String letterType = "letterType";
    public static final String presentationType = "presentationType";
    public static final String url = "url";
    public static final String university = "university";
    public static final String version = "version";
    public static final String volume = "volume";
    public static final String websiteTitle = "websiteTitle";
    public static final String websiteType = "websiteType";
}