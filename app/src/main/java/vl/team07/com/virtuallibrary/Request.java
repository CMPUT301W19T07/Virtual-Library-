/*
 * Copyright <2019-1-23> <Ronghui Shao>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vl.team07.com.virtuallibrary;

import java.util.ArrayList;

public class Request {

    private User Requester;
    private Book RequestedBook;

    public Request(User requester, Book requestedBook){
        this.Requester = requester;
        this.RequestedBook = requestedBook;
        if (this.RequestedBook.getRequesters().contains(requester)){
            throw new IllegalArgumentException("The requester:" + requester.getName() + " has already requested " + requestedBook.getTitle());
        }
        else{
            this.RequestedBook.addRequester(requester);
        }
    }

    public void acceptRequest(){
        this.RequestedBook.setStatus(BookStatus.BORROWED);
    }

    public void acceptRequest(ArrayList<Book> requestedBooks){
        this.RequestedBook.setStatus(BookStatus.BORROWED);
        requestedBooks.add(this.RequestedBook);
    }

    public void rejectRequest(){
        this.RequestedBook.getRequesters().remove(this.Requester);
        this.RequestedBook.setStatus(BookStatus.AVAILABLE);
    }

    public ArrayList<User> showRequester(){
        return this.RequestedBook.getRequesters();
    }

    public void clearRequester(){
        this.RequestedBook.getRequesters().clear();
    }

    public void notifyBeRequest(){
        // Notification to owner if book is requested
    }

    public void notifyRequestAccepted(){
        // Notification to borrower if request is accepted
    }
}