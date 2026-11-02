import { Component, ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MarkdownComponent } from 'ngx-markdown';

@Component({
  selector: 'app-chat',
  imports: [FormsModule,
            MarkdownComponent],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat {
  query: string = "";
  response: any;
  progress: boolean = false;

  constructor(private http: HttpClient,
              private cdr: ChangeDetectorRef) {
  }

  askAgent() {
    this.response = "";
    this.progress = true;
    this.http.get("http://localhost:8080/askAgent?query="+this.query,
      {responseType : 'text'})
    .subscribe({
      next : resp => {
        this.response = resp;
      },
      error : err => {
        console.log(err);
      },
      complete : () => {
        this.progress = false;
      }
    })
  }

  askStreamAgent() {
        if (!this.query.trim()) {
          return;
        }

    this.response = '';
    this.progress = true;
    this.cdr.detectChanges(); // Force l'affichage du spinner

    this.http.get(
      `http://localhost:8080/askStreamAgent?query=${encodeURIComponent(this.query)}`,
      {
        responseType: 'text',
        observe: "events",
        reportProgress: true
      }
    ).subscribe({
      next: (evt) => {
        if (evt.type === HttpEventType.DownloadProgress) {
          const partialText = (evt as HttpDownloadProgressEvent).partialText;

          if (partialText) {
            this.response = partialText;
            this.cdr.detectChanges(); // Force l'update à chaque chunk
          }
        }
      },
      error: (error) => {
        console.error('Erreur:', error);
        this.response = '❌ Erreur lors de la communication avec le serveur';
        this.progress = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        this.progress = false;
        this.cdr.detectChanges();
      },
    });
  }

}
