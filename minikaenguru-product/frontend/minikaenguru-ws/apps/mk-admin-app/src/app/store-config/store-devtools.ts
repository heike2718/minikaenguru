
import { StoreDevtoolsModule } from "@ngrx/store-devtools";


export const StoreDevModules = [
    StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: false })
];


//export const StoreDevModules = [];

