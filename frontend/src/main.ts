import { registerLocaleData } from '@angular/common';
import { bootstrapApplication } from '@angular/platform-browser';
import localeEnIn from '@angular/common/locales/en-IN';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

registerLocaleData(localeEnIn);

bootstrapApplication(AppComponent, appConfig).catch((error: unknown) => console.error(error));
