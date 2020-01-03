<<<<<<< HEAD
# minikaenguru

Monorepo für alles, was mit Minikänguru zu tun hat.
=======
# mkadmin-app
admin app for the mathematical minikangaroo competition: (https://mathe-jung-alt.de/minikaenguru/index.html)

## Dinge

### Wenn ein Feature-State eines Moduls nicht im globalen State auftaucht

Schauen, dass er im Modul deklariert wurde:

	@NgModule({
	imports: [
		CommonModule,
		RouterModule.forChild(aboutRoutes),
		StoreModule.forFeature(fromAbout.aboutFeatureKey, fromAbout.authReducer),
		EffectsModule.forFeature([AboutEffects])
	],

## Relesenotes

[Release-Notes](RELEASE-NOTES.md)


>>>>>>> mkadmin-app/master
