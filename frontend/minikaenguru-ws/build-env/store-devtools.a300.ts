import { StoreDevtoolsModule } from "@ngrx/store-devtools";

export const StoreDevModules = [
    StoreDevtoolsModule.instrument({ maxAge: 5, logOnly: false })
];
